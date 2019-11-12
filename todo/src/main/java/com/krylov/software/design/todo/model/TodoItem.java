package com.krylov.software.design.todo.model;

import com.krylov.software.design.todo.dto.TodoItemDto;

public class TodoItem {

    private final long id;
    private final String title;
    private final String text;
    private final TodoStatus todoStatus;

    public TodoItem(long id, String title, String text, TodoStatus todoStatus) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.todoStatus = todoStatus;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public TodoStatus getTodoStatus() {
        return todoStatus;
    }

    public TodoItem updateStatus(TodoStatus todoStatus) {
        return new TodoItem(id, title, text, todoStatus);
    }

    public TodoItemDto toDto() {
        return new TodoItemDto(title, text, todoStatus);
    }
}
