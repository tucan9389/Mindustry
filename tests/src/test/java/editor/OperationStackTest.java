package editor;

import io.anuke.mindustry.editor.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


public class OperationStackTest {

    MapEditor editor = new MapEditor();
    OperationStack stack = new OperationStack();

    @BeforeEach
    void setup() {
        stack.clear();
    }

    @Test
    void testClear0() {
        // given
        DrawOperation expectedValue = null;

        // when
        stack.clear();
        DrawOperation sut = stack.undo();

        // then
        assertEquals(sut, expectedValue);
    }

    @Test
    void testClear1() {
        // given
        DrawOperation expectedValue = null;

        // when
        stack.add(new DrawOperation(editor));
        stack.clear();
        DrawOperation sut = stack.undo();

        // then
        assertEquals(sut, expectedValue);
    }

    @Test
    void testClear2() {
        // given
        DrawOperation expectedValue = null;

        // when
        for (int i=0; i<11; i++) {
            stack.add(new DrawOperation(editor));
        }
        stack.clear();
        DrawOperation sut = stack.undo();

        // then
        assertEquals(sut, expectedValue);
    }

    @Test
    void testUndo0() {
        // given
        DrawOperation expectedValue = new DrawOperation(editor);

        // when
        stack.add(expectedValue);
        stack.add(new DrawOperation(editor));
        DrawOperation sut = stack.undo();

        // then
        assertEquals(sut, expectedValue);
    }

    @Test
    void testUndo1() {
        // given
        DrawOperation expectedValue = null;

        // when
        stack.add(new DrawOperation(editor));
        DrawOperation sut = stack.undo();

        // then
        assertEquals(sut, expectedValue);
    }
}
