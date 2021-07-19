package com.byted.camp.todolist.beans;

public enum State {
    TODO(0), DONE(1);

    public final int intValue;

    State(int intValue) {
        this.intValue = intValue;
    }

    // 即给定一个intValue，返回一个State的类
    public static State from(int intValue) {
        for (State state : State.values()) {
            if (state.intValue == intValue) {
                return state;
            }
        }
        return TODO; // default
    }
}
