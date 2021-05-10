package com.zarbosoft.merman.jfxeditor1;

import com.zarbosoft.merman.core.Context;
import com.zarbosoft.merman.core.Environment;
import com.zarbosoft.merman.core.Hoverable;
import com.zarbosoft.merman.core.SyntaxPath;
import com.zarbosoft.merman.core.document.Atom;
import com.zarbosoft.merman.core.document.Document;
import com.zarbosoft.merman.core.document.fields.FieldArray;
import com.zarbosoft.merman.core.document.fields.FieldPrimitive;
import com.zarbosoft.merman.core.example.JsonSyntax;
import com.zarbosoft.merman.core.hid.ButtonEvent;
import com.zarbosoft.merman.core.hid.Key;
import com.zarbosoft.merman.core.syntax.Syntax;
import com.zarbosoft.merman.core.syntax.style.Padding;
import com.zarbosoft.merman.core.visual.visuals.ArrayCursor;
import com.zarbosoft.merman.core.visual.visuals.VisualFrontArray;
import com.zarbosoft.merman.core.visual.visuals.VisualFrontAtomBase;
import com.zarbosoft.merman.core.visual.visuals.VisualFrontPrimitive;
import com.zarbosoft.merman.editorcore.Editor;
import com.zarbosoft.merman.editorcore.EditorCursorFactory;
import com.zarbosoft.merman.editorcore.history.History;
import com.zarbosoft.merman.jfxcore.JFXEnvironment;
import com.zarbosoft.merman.jfxcore.display.JavaFXDisplay;
import com.zarbosoft.merman.jfxcore.serialization.JavaSerializer;
import com.zarbosoft.merman.jfxeditor1.modalinput.ModalArrayCursor;
import com.zarbosoft.merman.jfxeditor1.modalinput.ModalAtomCursor;
import com.zarbosoft.merman.jfxeditor1.modalinput.ModalPrimitiveCursor;
import com.zarbosoft.pidgoon.errors.GrammarTooUncertainAt;
import com.zarbosoft.pidgoon.errors.InvalidStreamAt;
import com.zarbosoft.pidgoon.errors.NoResults;
import com.zarbosoft.pidgoon.events.Position;
import com.zarbosoft.pidgoon.model.MismatchCause;
import com.zarbosoft.pidgoon.model.Step;
import com.zarbosoft.rendaw.common.Assertion;
import com.zarbosoft.rendaw.common.Format;
import com.zarbosoft.rendaw.common.ROList;
import com.zarbosoft.rendaw.common.ROMap;
import com.zarbosoft.rendaw.common.ROSet;
import com.zarbosoft.rendaw.common.TSList;
import com.zarbosoft.rendaw.common.TSMap;
import com.zarbosoft.rendaw.common.TSSet;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class NotMain extends Application {
  public static final ROSet<Key> controlKeys =
      TSSet.of(Key.CONTROL, Key.CONTROL_LEFT, Key.CONTROL_RIGHT).ro();
  public static final ROSet<Key> shiftKeys =
      TSSet.of(Key.SHIFT, Key.SHIFT_LEFT, Key.SHIFT_RIGHT).ro();
  private final ScheduledThreadPoolExecutor worker = new ScheduledThreadPoolExecutor(1);
  public DragSelectState dragSelect;
  private String path;
  private Editor editor;

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

  @Override
  public void start(Stage primaryStage) throws Exception {
    try {
      List<String> args = getParameters().getUnnamed();
      if (args.isEmpty())
        throw new RuntimeException("Need to specify one file to open on the command line");

      Environment env = new JFXEnvironment(Locale.getDefault());
      JavaSerializer serializer;
      ROMap<String, Syntax> syntaxes =
          new TSMap<String, Syntax>().put("json", JsonSyntax.create(env, new Padding(5, 5, 5, 5)));

      path = args.get(0);
      Document document;
      String extension = extension(path);
      Syntax syntax =
          syntaxes.getOr(
              extension,
              () -> {
                throw new RuntimeException(
                    Format.format("No syntax for files with extension [%s]", extension));
              });
      serializer = new JavaSerializer(syntax.backType);
      try {
        document = serializer.loadDocument(syntax, Files.readAllBytes(Paths.get(path)));
      } catch (NoSuchFileException e) {
        document = new Document(syntax, Editor.createEmptyAtom(syntax, syntax.root));
      }

      JavaFXDisplay display = new JavaFXDisplay(syntax);
      editor =
          new Editor(
              syntax,
              document,
              display,
              env,
              new History(),
              serializer,
              e ->
                  new EditorCursorFactory(e) {
                    @Override
                    public VisualFrontPrimitive.Cursor createPrimitiveCursor(
                        Context context,
                        VisualFrontPrimitive visualPrimitive,
                        boolean leadFirst,
                        int beginOffset,
                        int endOffset) {
                      return new ModalPrimitiveCursor(
                          context,
                          visualPrimitive,
                          leadFirst,
                          beginOffset,
                          endOffset,
                          NotMain.this);
                    }

                    @Override
                    public VisualFrontAtomBase.Cursor createAtomCursor(
                        Context context, VisualFrontAtomBase base) {
                      return new ModalAtomCursor(context, base, NotMain.this);
                    }

                    @Override
                    public ArrayCursor createArrayCursor(
                        Context context,
                        VisualFrontArray visual,
                        boolean leadFirst,
                        int start,
                        int end) {
                      return new ModalArrayCursor(
                          context, visual, leadFirst, start, end, NotMain.this);
                    }
                  },
              new Editor.Config(new Context.InitialConfig()));
      editor.context.document.root.visual.selectAnyChild(editor.context);

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
                    ((Atom) base).fieldParentRef.selectValue(context);
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
      primaryStage.getIcons().add(new Image(new ByteArrayInputStream(Embedded.icon48)));
      primaryStage.setScene(new Scene(display.node, 800, 600));
      primaryStage.show();
      primaryStage.setOnCloseRequest(
          windowEvent -> {
            flush(false);
            worker.shutdown();
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
      for (Step.Branch leaf : (TSList<Step.Branch>) e.e.step.branches) {
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
      throw new RuntimeException("\n" + writer.toString());
    }
  }

  public void flush(boolean clearBackup) {
    String backupPath = path + ".merman_backup";
    if (editor.history.isModified()) {
      if (!clearBackup) {
        try {
          Files.copy(Paths.get(path), Paths.get(backupPath));
        } catch (FileAlreadyExistsException e) {
          // nop
        } catch (IOException e) {
          logException(e, "Failed to write backup to %s", backupPath);
        }
      }
      try {
        Files.write(
            Paths.get(path),
            (byte[]) editor.context.serializer.write(editor.context.document.root));
      } catch (IOException e) {
        logException(e, "Failed to write to %s", path);
        return;
      }
      editor.history.clearModified();
    }
    try {
      Files.deleteIfExists(Paths.get(backupPath));
    } catch (IOException e) {
      logException(e, "Failed to clean up backup %s", backupPath);
    }
  }

  public static class DragSelectState {
    public final SyntaxPath start;
    public SyntaxPath end;

    public DragSelectState(SyntaxPath start) {
      this.start = start;
    }
  }
}
