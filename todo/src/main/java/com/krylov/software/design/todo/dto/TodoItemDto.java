package com.krylov.software.design.todo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.krylov.software.design.todo.model.TodoStatus;
import org.springframework.lang.Nullable;

public class TodoItemDto {
    private final String title;
    private final String text;
    @Nullable
    private final TodoStatus todoStatus;

    public TodoItemDto(
            @JsonProperty("title") String title,
            @JsonProperty("text") String text
    ) {
        this(title, text, null);
    }

    public TodoItemDto(String title, String text, TodoStatus todoStatus) {
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
}
