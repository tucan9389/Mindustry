package io.anuke.mindustry.editor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperationStackTest {

    OperationStack stack;
    MapEditor editor;

    @BeforeEach
    void setUp() {
        editor = new MapEditor();
        stack = new OperationStack();

        for (int i=0; i<10; i++) {
            stack.add(new DrawOperation(editor));
        }
    }

    @Test
    void basicOperate() {
        stack.undo();
        stack.undo();
        stack.add(new DrawOperation(editor));
        stack.undo();
        stack.undo();
        stack.add(new DrawOperation(editor));
        stack.add(new DrawOperation(editor));
        stack.undo();
        stack.undo();
    }
}