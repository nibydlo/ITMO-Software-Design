package com.krylov.software.design.todo.controller;

import com.krylov.software.design.todo.dto.TodoItemDto;
import com.krylov.software.design.todo.model.TodoItem;
import com.krylov.software.design.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TodoController {
    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/all")
    public List<TodoItemDto> getTodoList() {
        return todoService.getTodoList()
                .stream()
                .map(TodoItem::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/add")
    public void addTodoList(
            @RequestBody List<TodoItemDto> todoList
    ) {
        todoService.addTodoList(todoList);
    }

    @PostMapping("/remove")
    public void removeTodoList(
            @RequestBody List<Long> todoIds
    ) {
        todoService.removeTodoListByIds(todoIds);
    }

    @PatchMapping("/mark")
    public void markTodo(
            @RequestParam long todoId
    ) {
        todoService.markTodoItem(todoId);
    }
}
