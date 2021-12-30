package com.zarbosoft.alligatoroid.compiler.modules.sourcediskcache;

import com.zarbosoft.alligatoroid.compiler.model.ids.BundleModuleSubId;
import com.zarbosoft.alligatoroid.compiler.CompileContext;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.ids.LocalModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.model.ids.ModuleId;
import com.zarbosoft.alligatoroid.compiler.model.ids.RemoteModuleId;
import com.zarbosoft.alligatoroid.compiler.Utils;
import com.zarbosoft.alligatoroid.compiler.model.error.CacheUnexpectedPre;
import com.zarbosoft.alligatoroid.compiler.model.error.ImportNotFoundPre;
import com.zarbosoft.alligatoroid.compiler.model.error.RemoteModuleHashMismatchPre;
import com.zarbosoft.alligatoroid.compiler.model.error.RemoteModuleHashMismatch;
import com.zarbosoft.alligatoroid.compiler.model.error.RemoteModuleProtocolUnsupported;
import com.zarbosoft.alligatoroid.compiler.model.error.WarnUnexpected;
import com.zarbosoft.alligatoroid.compiler.modules.Source;
import com.zarbosoft.alligatoroid.compiler.modules.SourceResolver;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Common;
import com.zarbosoft.rendaw.common.ROList;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class SourceDiskCache implements SourceResolver {
  private static final Pattern fsSafeSegment = Pattern.compile("[a-zA-Z0-9._-]+");
  private static final Pattern fsSafeSegmentEscape = Pattern.compile("__");
  public final Path rootCachePath;

  public SourceDiskCache(Path rootCachePath) {
    this.rootCachePath = rootCachePath;
  }

  private final String safetenFsSegment(String segment) {
    segment = fsSafeSegmentEscape.matcher(segment).replaceAll("___");
    final Matcher matcher = fsSafeSegment.matcher(segment);
    if (matcher.matches()) return segment;
    String hash = new Utils.SHA256().add(segment).buildHex().substring(0, 5);
    matcher.reset();
    StringBuilder out = new StringBuilder();
    while (matcher.find()) {
      out.append("__");
      out.append(segment.substring(matcher.start(), matcher.end()));
    }
    out.append(segment.substring(matcher.end(), segment.length()));
    out.append("__");
    out.append(hash);
    return out.toString();
  }

  @Override
  public Source get(CompileContext context, ModuleId id) {
    return id.dispatch(
        new ModuleId.Dispatcher<Source>() {
          @Override
          public Source handle(LocalModuleId id) {
            final Path path = Paths.get(id.path).toAbsolutePath().normalize();
            final Utils.SHA256 hash = new Utils.SHA256().add(path);
            return new Source(hash.buildHex(), path);
          }

          @Override
          public Source handle(RemoteModuleId id) {
            final ROList<String> splits = Common.splitN(id.url, "://", 2);
            String proto = splits.get(0);
            switch (proto) {
              case "file":
                {
                  Path downloadPath = Paths.get(splits.get(1));
                  String downloadHash;
                  try {
                    downloadHash = new Utils.SHA256().add(downloadPath).buildHex();
                  } catch (Common.UncheckedFileNotFoundException e) {
                    throw new ImportNotFoundPre(id.url);
                  } catch (Exception e) {
                    throw new CacheUnexpectedPre(downloadPath.toString(), e);
                  }
                  if (!downloadHash.equals(id.hash)) {
                    throw new RemoteModuleHashMismatchPre(id.url, id.hash, downloadHash);
                  }
                  return new Source(id.hash, downloadPath);
                }
              case "http":
              case "https":
                {
                  final HttpUrl url = HttpUrl.parse(id.url);
                  Path downloadDir = rootCachePath.resolve(proto);
                  final List<String> urlPath = url.pathSegments();
                  for (String seg : urlPath) {
                    downloadDir = downloadDir.resolve(safetenFsSegment(seg));
                  }
                  switch (proto) {
                    case "http":
                      {
                        if (url.port() != 80) {
                          downloadDir.resolve(Integer.toString(url.port()));
                        }
                        break;
                      }
                    case "https":
                      {
                        if (url.port() != 443) {
                          downloadDir.resolve(Integer.toString(url.port()));
                        }
                        break;
                      }
                    default:
                      throw new Assertion();
                  }
                  if (url.query() != null) downloadDir.resolve(safetenFsSegment(url.query()));
                  Path downloadPath =
                      downloadDir.resolve(Paths.get(urlPath.get(urlPath.size() - 1)).getFileName());

                  // Check existing file
                  {
                    String downloadHash;
                    do {
                      try {
                        downloadHash = new Utils.SHA256().add(downloadPath).buildHex();
                      } catch (Common.UncheckedFileNotFoundException e) {
                        // nop
                        break;
                      } catch (Exception e) {
                        context.logger.warn(new WarnUnexpected(downloadPath.toString(), e));
                        break;
                      }
                      if (downloadHash.equals(id.hash)) {
                        return new Source(id.hash, downloadPath);
                      }
                    } while (false);
                  }

                  // No/bad existing file - download
                  {
                    String downloadHash =
                        uncheck(
                            () -> {
                              OkHttpClient client = new OkHttpClient();
                              try (Response response =
                                  client.newCall(new Request.Builder().get().build()).execute()) {
                                Files.copy(response.body().byteStream(), downloadPath);
                              }
                              return new Utils.SHA256().add(downloadPath).buildHex();
                            });
                    if (!downloadHash.equals(id.hash)) {
                      throw new Error.PreError() {
                        @Override
                        public Error toError(Location location) {
                          return new RemoteModuleHashMismatch(
                              location, id.url, id.hash, downloadHash);
                        }
                      };
                    }
                    return new Source(id.hash, downloadPath);
                  }
                }
              default:
                throw new Error.PreError() {
                  @Override
                  public Error toError(Location location) {
                    return new RemoteModuleProtocolUnsupported(location, id.url);
                  }
                };
            }
          }

          @Override
          public Source handle(BundleModuleSubId id) {
            return get(context, id.module);
          }
        });
  }
}
