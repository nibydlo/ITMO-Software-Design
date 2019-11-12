package com.krylov.software.design.todo.dao;

import com.krylov.software.design.todo.dto.TodoItemDto;
import com.krylov.software.design.todo.model.TodoItem;
import com.krylov.software.design.todo.model.TodoStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TodoDao {
    private Long idCounter = 0L;
    private final Map<Long, TodoItem> storage;

    public TodoDao() {
        this.storage = new HashMap<>();
    }

    public List<TodoItem> getTodoList() {
        return List.copyOf(storage.values());
    }

    public void addTodo(TodoItemDto dto) {
        storage.put(
                idCounter,
                new TodoItem(idCounter++, dto.getTitle(), dto.getText(), TodoStatus.NEW)
        );
    }

    public void removeTodo(long entityId) {
        storage.remove(entityId);
    }

    public void markTodo(long entityId, TodoStatus status) {
        TodoItem todoItem = storage.get(entityId);
        storage.replace(
                entityId,
                todoItem.updateStatus(status)
        );
    }
}
