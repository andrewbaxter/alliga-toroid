package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.alligatoroid.compiler.Alligatorus;
import com.zarbosoft.alligatoroid.compiler.model.error.Error;
import com.zarbosoft.alligatoroid.compiler.model.error.WarnUnexpected;
import com.zarbosoft.alligatoroid.compiler.model.ids.ImportId;
import com.zarbosoft.alligatoroid.compiler.model.ids.Location;
import com.zarbosoft.alligatoroid.compiler.modules.StderrLogger;
import com.zarbosoft.luxem.read.path.LuxemPath;
import com.zarbosoft.merman.core.BackPath;
import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.Hoverable;
import com.zarbosoft.merman.core.Stylist;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.display.Blank;
import com.zarbosoft.merman.core.display.Display;
import com.zarbosoft.merman.core.display.Text;
import com.zarbosoft.merman.core.display.TextStylable;
import com.zarbosoft.merman.core.display.derived.CourseGroup;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.document.Document;
import com.zarbosoft.merman.core.document.fields.FieldArray;
import com.zarbosoft.merman.core.document.fields.FieldPrimitive;
import com.zarbosoft.merman.core.example.DirectStylist;
import com.zarbosoft.merman.core.example.JsonSyntax;
import com.zarbosoft.merman.core.example.SyntaxOut;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.core.hid.Key;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.merman.core.syntax.style.ObboxStyle;
import com.zarbosoft.merman.core.syntax.style.Padding;
import com.zarbosoft.merman.core.syntax.symbol.SymbolTextSpec;
import com.zarbosoft.merman.core.visual.visuals.VisualAtom;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldArray;
import com.zarbosoft.merman.core.visual.visuals.VisualFieldPrimitive;
import com.zarbosoft.merman.core.wall.Brick;
import com.zarbosoft.merman.core.wall.bricks.BrickEmpty;
import com.zarbosoft.merman.core.wall.bricks.BrickText;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.EditorCursorFactory;
import com.zarbosoft.merman.editorcore.cursors.BaseEditCursorFieldPrimitive;
import com.zarbosoft.merman.editorcore.gap.BaseEditCursorGapFieldPrimitive;
import com.zarbosoft.merman.editorcore.history.FileIds;
import com.zarbosoft.merman.editorcore.history.History;
import com.zarbosoft.merman.editorcore.history.ModifiedStateListener;
import com.zarbosoft.merman.jfxcore.JFXEnvironment;
import com.zarbosoft.merman.jfxcore.display.JavaFXDisplay;
import com.zarbosoft.merman.jfxcore.serialization.JavaSerializer;
import com.zarbosoft.pidgoon.errors.GrammarTooUncertainAt;
import com.zarbosoft.pidgoon.errors.InvalidStreamAt;
import com.zarbosoft.pidgoon.errors.NoResults;
import com.zarbosoft.pidgoon.events.Position;
import com.zarbosoft.pidgoon.model.Leaf;
import com.zarbosoft.pidgoon.model.MismatchCause;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROSet;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.zarbosoft.rendaw.common.Common.uncheck;

public class NotMain extends Application {
  public static final ROSet<Key> controlKeys =
      TSSet.of(Key.CONTROL, Key.CONTROL_LEFT, Key.CONTROL_RIGHT).ro();
  public static final ROSet<Key> shiftKeys =
      TSSet.of(Key.SHIFT, Key.SHIFT_LEFT, Key.SHIFT_RIGHT).ro();
  public static final String META_KEY_ERROR = "error";
  public DragSelectState dragSelect;
  public Runnable flushCallback;
  public ErrorPage errorPage = new ErrorPage();
  private Path path;
  private Editor editor;
  private Thread compileThread;
  private Delay delayFlush = new Delay(500, () -> flush(false));

  public static void main(String[] args) {
    NotMain.launch(args);
  }

  public static String extension(String path) {
    int dot = path.lastIndexOf(".");
    if (dot == -1) return "";
    return path.substring(dot + 1);
  }

  public static void logException(Exception e, String s, Object... args) {
    System.out.format(s + "\n", args);
    e.printStackTrace();
  }

  public static boolean handleCommonNavigation(Editor editor, NotMain main, ButtonEvent hidEvent) {
    if (editor.details.handleKey(editor, hidEvent)) return true;
    if (hidEvent.press) {
      switch (hidEvent.key) {
        case F:
          {
            if (hidEvent.modifiers.containsAny(controlKeys)) {
              main.flush(true);
              return true;
            }
          }
        case Z:
          {
            if (hidEvent.modifiers.containsAny(controlKeys)) {
              if (hidEvent.modifiers.containsAny(shiftKeys)) {
                editor.redo();
              } else {
                editor.undo();
              }
              return true;
            }
          }
        case Y:
          {
            if (hidEvent.modifiers.containsAny(controlKeys)) {
              editor.redo();
              return true;
            }
          }
      }
    }
    return false;
  }

  public static boolean handlePrimitiveNavigation(
      Context context, NotMain main, BaseEditCursorFieldPrimitive cursor, ButtonEvent hidEvent) {
    final Editor editor = Editor.get(context);
    if (handleCommonNavigation(editor, main, hidEvent)) return true;
    if (hidEvent.press) {
      switch (hidEvent.key) {
        case ESCAPE:
          {
            cursor.editExit(editor);
            return true;
          }
        case DIR_DIVE:
          {
            if (hidEvent.modifiers.containsAny(controlKeys)) {
              if (hidEvent.modifiers.containsAny(shiftKeys)) {
                if (cursor.range.leadFirst) cursor.actionReleasePreviousWord(context);
                else cursor.actionGatherNextWord(context);
              } else cursor.actionNextWord(context);
            } else {
              if (hidEvent.modifiers.containsAny(shiftKeys)) {
                if (cursor.range.leadFirst) cursor.actionReleasePreviousGlyph(context);
                else cursor.actionGatherNextGlyph(context);
              } else cursor.actionNextGlyph(context);
            }
            return true;
          }
        case DIR_SURFACE:
          {
            if (hidEvent.modifiers.containsAny(controlKeys)) {
              if (hidEvent.modifiers.containsAny(shiftKeys)) {
                if (cursor.range.leadFirst) cursor.actionGatherPreviousWord(context);
                else cursor.actionReleaseNextWord(context);
              } else cursor.actionPreviousWord(context);
            } else {
              if (hidEvent.modifiers.containsAny(shiftKeys)) {
                if (cursor.range.leadFirst) cursor.actionGatherPreviousGlyph(context);
                else cursor.actionReleaseNextGlyph(context);
              } else cursor.actionPreviousGlyph(context);
            }
            return true;
          }
        case HOME:
          {
            cursor.actionLineBegin(context);
            return true;
          }
        case END:
          {
            cursor.actionLineEnd(context);
            return true;
          }
        case BACK_SPACE:
          {
            if (hidEvent.modifiers.containsAny(controlKeys)) {
              cursor.editCut(editor);
            } else {
              cursor.editDeletePrevious(editor);
            }
            return true;
          }
        case DELETE:
          {
            if (hidEvent.modifiers.containsAny(controlKeys)) {
              cursor.editCut(editor);
            } else {
              cursor.editDeleteNext(editor);
            }
            return true;
          }
        case X:
          {
            if (hidEvent.modifiers.containsAny(controlKeys)) {
              if (cursor.range.beginOffset != cursor.range.endOffset) cursor.editCut(editor);
              return true;
            }
            break;
          }
        case V:
          {
            if (hidEvent.modifiers.containsAny(controlKeys)) {
              cursor.editPaste(editor);
              return true;
            }
            break;
          }
      }
    }
    return false;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    try {
      List<String> args = getParameters().getUnnamed();
      if (args.isEmpty())
        throw new RuntimeException("Need to specify one file to open on the command line");

      Environment env = new JFXEnvironment(Locale.getDefault());

      JavaSerializer serializer;
      ROMap<String, AlligatorusSyntax.EditorSyntaxOut> syntaxes =
          new TSMap<String, AlligatorusSyntax.EditorSyntaxOut>()
              .put(
                  "json",
                  new AlligatorusSyntax.EditorSyntaxOut(
                      JsonSyntax.create(env, new Padding(5, 5, 5, 5)), new TSList<>()))
              .put("at", AlligatorusSyntax.create(env, new Padding(5, 5, 5, 5)));

      String rawPath = args.get(0);
      path = Paths.get(rawPath).toAbsolutePath();
      Document document;
      String extension = extension(rawPath);
      AlligatorusSyntax.EditorSyntaxOut editorSyntaxOut =
          syntaxes.getOr(
              extension,
              () -> {
                throw new RuntimeException(
                    Format.format("No syntax for files with extension [%s]", extension));
              });
      SyntaxOut syntaxOut = editorSyntaxOut.syntaxOut;
      Syntax syntax = syntaxOut.syntax;
      TSMap<Atom, WeakReference<MarkerBox>> markDisplays = new TSMap<>();
      Stylist stylist =
          new Stylist() {
            @Override
            public void styleEmpty(Context context, BrickEmpty brick) {
              syntaxOut.stylist.styleEmpty(context, brick);
              updateMarkDisplay(brick);
            }

            private void updateMarkDisplay(Brick brick) {
              if (editor == null) return;
              if (!brick.meta().has("mark")) return;
              Atom atom = brick.getVisual().atomVisual().atom;
              MarkerBox display = null;
              WeakReference<MarkerBox> ref = markDisplays.getOpt(atom);
              if (ref != null) display = ref.get();
              if (display == null) {
                display = new MarkerBox(editor.context, atom, syntaxOut.markTransverseOffset);
                brick.addAttachment(editor.context, display);
                markDisplays.putReplace(atom, new WeakReference<>(display));
              }
              display.update(editor.context);
            }

            @Override
            public void styleText(Context context, BrickText brick) {
              syntaxOut.stylist.styleText(context, brick);
              updateMarkDisplay(brick);
            }

            @Override
            public void styleObbox(Context context, ObboxStyle.Stylable obbox, ObboxType type) {
              syntaxOut.stylist.styleObbox(context, obbox, type);
            }

            @Override
            public void styleBannerText(Context context, Text text) {
              syntaxOut.stylist.styleBannerText(context, text);
            }

            @Override
            public void styleEmptyDisplay(
                Context context, Blank blank, ROMap<String, Object> meta) {
              syntaxOut.stylist.styleEmptyDisplay(context, blank, meta);
            }

            @Override
            public void styleTextDisplay(Context context, Text text, ROMap<String, Object> meta) {
              syntaxOut.stylist.styleTextDisplay(context, text, meta);
            }

            @Override
            public void styleChoiceDescription(
                Context context, TextStylable text, CourseGroup textPad) {
              syntaxOut.stylist.styleChoiceDescription(context, text, textPad);
            }

            @Override
            public void styleMarker(Context context, Text text, MarkerType type) {
              syntaxOut.stylist.styleMarker(context, text, type);
            }

            @Override
            public ObboxStyle tabStyle() {
              return syntaxOut.stylist.tabStyle();
            }
          };

      final HBox layout = new HBox();
      final Label messages = new Label();

      ImportId rootModuleSpec = Alligatorus.rootModuleSpec(path);
      TSList<Atom> errorAtoms = new TSList<>();
      if ("at".equals(extension)) {
        flushCallback =
            () -> {
              if (compileThread != null) {
                compileThread.interrupt();
                uncheck(() -> compileThread.join());
              }
              compileThread =
                  new Thread(
                      () -> {
                        Exception e0mut = null;
                        Alligatorus.Result modules0 = null;
                        try {
                          modules0 =
                              Alligatorus.compile(
                                  Alligatorus.defaultCachePath(),
                                  new StderrLogger(),
                                  rootModuleSpec);
                        } catch (Exception e1) {
                          e0mut = e1;
                        }
                        Exception e0 = e0mut;
                        Alligatorus.Result modules = modules0;
                        Platform.runLater(
                            () -> {
                              try {
                                layout.getChildren().remove(messages);
                                messages.setText("");
                                TSSet<Atom> changedAtoms = new TSSet<>();

                                // Clear existing errors
                                for (Atom atom : errorAtoms) {
                                  atom.metaRemove(editor.context, META_KEY_ERROR);
                                  changedAtoms.add(atom);
                                }
                                errorAtoms.clear();

                                // Attach new errors
                                if (e0 != null) {
                                  if (!layout.getChildren().contains(messages))
                                    layout.getChildren().add(messages);
                                  messages.setText(messages.getText() + "\n" + e0.toString());
                                } else {
                                  TSMap<Atom, TSList<Object>> errorMessages = new TSMap<>();
                                  for (Map.Entry<ImportId, ROList<Error>> module :
                                      modules.errors.entrySet()) {
                                    for (Error error : module.getValue()) {
                                      error.dispatch(
                                          new Error.Dispatcher<Object>() {
                                            @Override
                                            public Object handle(Error.LocationError e) {
                                              final Location location = e.location;
                                              if (!rootModuleSpec.moduleId.equal1(location.module))
                                                return null;
                                              Atom atom = editor.fileIdMap.getOpt(location.id);
                                              if (atom == null) {
                                                if (!layout.getChildren().contains(messages))
                                                  layout.getChildren().add(messages);
                                                System.out.format(
                                                    "unlocatable location error %s: %s\n",
                                                    e.location, e);
                                                messages.setText(
                                                    messages.getText()
                                                        + Format.format(
                                                            "\nLocation error at [%s]: %s",
                                                            e.location, e));
                                                return null;
                                              }
                                              errorAtoms.add(atom);
                                              errorMessages
                                                  .getCreate(atom, () -> new TSList<>())
                                                  .add(error);
                                              changedAtoms.add(atom);
                                              return null;
                                            }

                                            @Override
                                            public Object handle(Error.DeserializeError e) {
                                              if (!rootModuleSpec.moduleId.equals(
                                                  module.getKey().moduleId)) return null;
                                              TSList<BackPath.Element> backPath = new TSList<>();
                                              for (LuxemPath.Element element : e.backPath.data) {
                                                backPath.add(
                                                    new BackPath.Element(
                                                        element.index,
                                                        element.key,
                                                        element.typeCount));
                                              }
                                              Atom atom =
                                                  editor.context.backLocate(new BackPath(backPath));
                                              if (atom == null) {
                                                if (!layout.getChildren().contains(messages))
                                                  layout.getChildren().add(messages);
                                                System.out.format(
                                                    "unlocatable deserialize error %s: %s\n",
                                                    e.backPath, e);
                                                messages.setText(
                                                    messages.getText()
                                                        + Format.format(
                                                            "\nDeserialize error at [%s]: %s",
                                                            e.backPath, e));
                                                return null;
                                              }
                                              errorAtoms.add(atom);
                                              errorMessages
                                                  .getCreate(atom, () -> new TSList<>())
                                                  .add(error);
                                              changedAtoms.add(atom);
                                              return null;
                                            }
                                          });
                                    }
                                  }

                                  for (Map.Entry<Atom, TSList<Object>> e : errorMessages) {
                                    e.getKey()
                                        .metaPut(editor.context, META_KEY_ERROR, e.getValue());
                                  }

                                  for (Atom atom : changedAtoms) {
                                    MarkerBox display = null;
                                    WeakReference<MarkerBox> ref = markDisplays.getOpt(atom);
                                    if (ref != null) display = ref.get();
                                    if (display != null) {
                                      display.update(editor.context);
                                    }
                                  }
                                }
                              } catch (Exception e1) {
                                System.out.format("Error processing errors: %s\n", e1);
                              }
                            });
                      });
              compileThread.start();
            };
      }

      FileIds fileIds = new FileIds();
      serializer = new JavaSerializer(syntax.backType);
      try {
        document = serializer.loadDocument(syntax, Files.readAllBytes(path));
      } catch (NoSuchFileException e) {
        document = new Document(syntax, Editor.createEmptyAtom(syntax, fileIds, syntax.root));
      } catch (Exception e) {
        throw new RuntimeException(Format.format("Failed to load document %s", path), e);
      }

      JavaFXDisplay display = new JavaFXDisplay(syntax);
      editor =
          new Editor(
              syntax,
              fileIds,
              document,
              display,
              env,
              new History(),
              serializer,
              stylist,
              editorSyntaxOut.refactors,
              e ->
                  new EditorCursorFactory(e) {
                    @Override
                    public BaseEditCursorGapFieldPrimitive createGapCursor(
                        VisualFieldPrimitive visualPrimitive,
                        boolean leadFirst,
                        int beginOffset,
                        int endOffset) {
                      return new CursorGapFieldPrimitive(
                          editor, visualPrimitive, leadFirst, beginOffset, endOffset, NotMain.this);
                    }

                    @Override
                    public com.zarbosoft.merman.core.visual.visuals.CursorFieldPrimitive
                        createPrimitiveCursor1(
                            Context context,
                            VisualFieldPrimitive visualPrimitive,
                            boolean leadFirst,
                            int beginOffset,
                            int endOffset) {
                      return new CursorFieldPrimitive(
                          context,
                          visualPrimitive,
                          leadFirst,
                          beginOffset,
                          endOffset,
                          NotMain.this);
                    }

                    @Override
                    public com.zarbosoft.merman.core.visual.visuals.CursorFieldArray
                        createFieldArrayCursor(
                            Context context,
                            VisualFieldArray visual,
                            boolean leadFirst,
                            int start,
                            int end) {
                      return new CursorFieldArray(
                          context, visual, leadFirst, start, end, NotMain.this);
                    }

                    @Override
                    public com.zarbosoft.merman.core.visual.visuals.CursorAtom createAtomCursor(
                        Context context, VisualAtom base, int index) {
                      return new CursorAtom(context, base, index, NotMain.this);
                    }
                  },
              new Editor.Config(
                      new Context.InitialConfig().animateCoursePlacement(true).animateDetails(true))
                  .suffixOnPatternMismatch(syntaxOut.suffixOnPatternMismatch)
                  .bannerPad(Padding.ct(3, 3))
                  .choiceColumnSpace(8)
                  .detailsPad(Padding.ct(3, 3))
                  .detailsMaxTransverseSpan(40)
                  .gapPlaceholderSymbol(
                      new SymbolTextSpec(
                          new SymbolTextSpec.Config("▢")
                              .meta(
                                  DirectStylist.meta(
                                      new DirectStylist.TextStyle()
                                          .fontSize(5)
                                          .padding(Padding.ct(1, 0))
                                          .color(syntaxOut.choiceCursor))))));
      editor.context.document.root.visual.selectIntoAnyChild(editor.context);

      editor.history.addModifiedStateListener(
          new ModifiedStateListener() {
            @Override
            public void changed(boolean modified) {
              if (modified) delayFlush.trigger(editor.context);
            }
          });
      editor.context.addHoverListener(
          new Context.HoverListener() {
            @Override
            public void hoverChanged(Context context, Hoverable hover) {
              if (hover != null && dragSelect != null) {
                SyntaxPath endPath = hover.getSyntaxPath();
                if (!endPath.equals(dragSelect.end)) {
                  dragSelect.end = endPath;
                  ROList<String> endPathList = endPath.toList();
                  ROList<String> startPathList = dragSelect.start.toList();
                  int longestMatch = startPathList.longestMatch(endPathList);
                  // If hover paths diverge, it's either
                  // - at two depths in a single tree (parent and child): both paths are for an
                  // atom,
                  // so longest match == parent == atom
                  // - at two subtrees of an array/primitives: longest submatch == array/primitive
                  // ==
                  // field, next segment == int
                  Object base =
                      context.syntaxLocate(new SyntaxPath(endPathList.subUntil(longestMatch)));
                  if (base instanceof FieldArray) {
                    int startIndex = Integer.parseInt(startPathList.get(longestMatch));
                    int endIndex = Integer.parseInt(endPathList.get(longestMatch));
                    if (endIndex < startIndex) {
                      ((FieldArray) base).selectInto(context, true, endIndex, startIndex);
                    } else {
                      ((FieldArray) base).selectInto(context, false, startIndex, endIndex);
                    }
                  } else if (base instanceof FieldPrimitive) {
                    // If end/start paths are the same then the longest match includes the index
                    // vs if they're different, then it includes the primitive but not index
                    // Adjust so the index is the next element in both cases
                    if (longestMatch == startPathList.size()) longestMatch -= 1;
                    int startIndex = Integer.parseInt(startPathList.get(longestMatch));
                    int endIndex = Integer.parseInt(endPathList.get(longestMatch));
                    if (endIndex < startIndex) {
                      ((FieldPrimitive) base).selectInto(context, true, endIndex, startIndex);
                    } else {
                      ((FieldPrimitive) base).selectInto(context, false, startIndex, endIndex);
                    }
                  } else if (base instanceof Atom) {
                    ((Atom) base).fieldParentRef.selectField(context);
                  } else throw new Assertion();
                }
              }
            }
          });
      editor.context.mouseButtonEventListener =
          new Context.KeyListener() {
            @Override
            public boolean handleKey(Context context, ButtonEvent e) {
              if (!e.press) {
                switch (e.key) {
                  case MOUSE_1:
                    {
                      if (dragSelect != null) {
                        dragSelect = null;
                        return true;
                      }
                    }
                  default:
                    return false;
                }
              } else {
                switch (e.key) {
                  case MOUSE_1:
                    {
                      if (context.hover != null) {
                        SyntaxPath path = context.hover.getSyntaxPath();
                        context.hover.select(context);
                        dragSelect = new DragSelectState(path);
                        return true;
                      } else if (context.cursor != null) {
                        SyntaxPath path = context.cursor.getSyntaxPath();
                        dragSelect = new DragSelectState(path);
                        return true;
                      }
                    }
                }
              }
              return false;
            }
          };
      editor.context.display.addScrollListener(
          new Display.ScrollListener() {
            @Override
            public void changed(double converse, double transverse) {
              editor.context.scroll = editor.context.scroll + transverse;
              editor.context.applyScroll();
            }
          });
      flushCallback.run();

      layout.getChildren().add(display.node);
      HBox.setHgrow(display.node, Priority.ALWAYS);
      messages.setMinWidth(100);
      messages.setWrapText(true);

      primaryStage
          .getIcons()
          .addAll(
              new Image(new ByteArrayInputStream(Embedded.icon128)),
              new Image(new ByteArrayInputStream(Embedded.icon64)),
              new Image(new ByteArrayInputStream(Embedded.icon16)));
      primaryStage.setScene(new Scene(layout, 800, 600));
      primaryStage.setTitle(path + " - merman1");
      primaryStage.show();
      primaryStage.setOnCloseRequest(
          windowEvent -> {
            flush(false);
            env.destroy();
          });
      primaryStage
          .focusedProperty()
          .addListener(
              new ChangeListener<Boolean>() {
                @Override
                public void changed(
                    ObservableValue<? extends Boolean> observable,
                    Boolean oldValue,
                    Boolean newValue) {
                  if (!newValue && oldValue) {
                    flush(false);
                  }
                }
              });

    } catch (GrammarTooUncertainAt e) {
      StringBuilder message = new StringBuilder();
      for (Leaf leaf : (TSList<Leaf>) e.e.step.leaves) {
        message.append(Format.format(" * %s (%s)\n", leaf, leaf.color()));
      }
      throw new RuntimeException(
          Format.format(
              "Too much uncertainty while parsing!\nat %s %s\n%s branches:\n%s",
              ((Position) e.at).at, ((Position) e.at).event, message.toString()));
    } catch (InvalidStreamAt e) {
      StringBuilder message = new StringBuilder();
      for (MismatchCause error : (TSList<MismatchCause>) e.step.errors) {
        message.append(Format.format(" * %s\n", error));
      }
      throw new RuntimeException(
          Format.format(
              "Document doesn't conform to syntax tree\nat %s %s\nmismatches at final stream element:\n%s",
              ((Position) e.at).at, ((Position) e.at).event, message.toString()));
    } catch (NoResults e) {
      StringBuilder message = new StringBuilder();
      for (MismatchCause error : (TSList<MismatchCause>) e.state.errors) {
        message.append(Format.format(" * %s\n", error));
      }
      throw new RuntimeException(
          Format.format("Document incomplete\nexpected:\n%s", message.toString()));
    } catch (RuntimeException e) {
      StringWriter writer = new StringWriter();
      e.printStackTrace(new PrintWriter(writer));
      throw new RuntimeException("\n" + writer);
    }
  }

  public void flush(boolean clearBackup) {
    Path backupPath = Paths.get((path.startsWith(".") ? "" : ".") + path + ".merman_backup");
    boolean wasModified = editor.history.isModified();
    if (wasModified) {
      Path path1 = this.path;
      if (!clearBackup && !Files.exists(backupPath)) {
        try {
          Files.copy(path1, backupPath);
        } catch (NoSuchFileException e) {
          // nop
        } catch (IOException e) {
          logException(e, "Failed to write backup to %s", backupPath);
        }
      }
      try {
        Files.write(
            path1,
            (byte[])
                editor.context.serializer.writeDocument(
                    editor.context.env, editor.context.document));
      } catch (IOException e) {
        logException(e, "Failed to write to %s", this.path);
        return;
      }
      editor.history.clearModified();
    }
    if (clearBackup)
      try {
        Files.deleteIfExists(backupPath);
      } catch (IOException e) {
        logException(e, "Failed to clean up backup %s", backupPath);
      }
    if (wasModified && flushCallback != null) flushCallback.run();
  }

  public static class DragSelectState {
    public final SyntaxPath start;
    public SyntaxPath end;

    public DragSelectState(SyntaxPath start) {
      this.start = start;
    }
  }
}
