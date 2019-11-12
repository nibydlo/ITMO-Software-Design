package com.krylov.software.design.todo.service;

import com.krylov.software.design.todo.dao.TodoDao;
import com.krylov.software.design.todo.dto.TodoItemDto;
import com.krylov.software.design.todo.model.TodoItem;
import com.krylov.software.design.todo.model.TodoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private final TodoDao todoDao;

    @Autowired
    public TodoService(TodoDao todoDao) {
        this.todoDao = todoDao;
    }

    public List<TodoItem> getTodoList() {
        return todoDao.getTodoList();
    }

    public void addTodoList(List<TodoItemDto> todoList) {
        todoList.forEach(todoDao::addTodo);
    }

    public void markTodoItem(Long todoId) {
        todoDao.markTodo(todoId, TodoStatus.COMPLETED);
    }

    public void removeTodoListByIds(List<Long> ids) {
        ids.forEach(todoDao::removeTodo);
    }
}
