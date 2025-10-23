package com.example.expense_service;

import com.example.expense_service.Controller.ExpenseController;
import com.example.expense_service.DTO.ExpenseDTO;
import com.example.expense_service.Service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;

    private UUID expenseId;
    private UUID userId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        expenseId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    public void testGetAllExpenses() {
        ExpenseDTO dto1 = new ExpenseDTO(expenseId, userId, LocalDate.now(), BigDecimal.valueOf(10000), UUID.randomUUID());
        ExpenseDTO dto2 = new ExpenseDTO(UUID.randomUUID(), userId, LocalDate.now(), BigDecimal.valueOf(20000), UUID.randomUUID());

        when(expenseService.getAllExpenses()).thenReturn(Arrays.asList(dto1, dto2));

        List<ExpenseDTO> result = expenseController.getAllExpenses();

        assertEquals(2, result.size());
        assertEquals(dto1.getSpend(), result.get(0).getSpend());
    }

    @Test
    public void testGetAllExpensesByUserId() {
        UUID userId = UUID.randomUUID();
        ExpenseDTO dto1 = new ExpenseDTO(UUID.randomUUID(), userId, LocalDate.now(), BigDecimal.valueOf(15000), UUID.randomUUID());
        ExpenseDTO dto2 = new ExpenseDTO(UUID.randomUUID(), userId, LocalDate.now(), BigDecimal.valueOf(20000), UUID.randomUUID());

        List<ExpenseDTO> mockList = Arrays.asList(dto1, dto2);

        when(expenseService.getAllExpensesByUserId(userId)).thenReturn(mockList);

        List<ExpenseDTO> result = expenseController.getAllExpensesByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(userId, result.get(1).getUserId());
    }


    @Test
    public void testCreateExpense() {
        ExpenseDTO requestDto = new ExpenseDTO(null, userId, LocalDate.now(), BigDecimal.valueOf(30000), UUID.randomUUID());
        ExpenseDTO responseDto = new ExpenseDTO(expenseId, userId, LocalDate.now(), BigDecimal.valueOf(30000), requestDto.getCateId());

        when(expenseService.createExpense(requestDto)).thenReturn(responseDto);

        ExpenseDTO result = expenseController.createExpense(requestDto);

        assertNotNull(result);
        assertEquals(responseDto.getExpenseId(), result.getExpenseId());
    }

    @Test
    public void testUpdateExpense() {
        ExpenseDTO requestDto = new ExpenseDTO(null, userId, LocalDate.now(), BigDecimal.valueOf(5000), UUID.randomUUID());
        ExpenseDTO updatedDto = new ExpenseDTO(expenseId, userId, requestDto.getDate(), requestDto.getSpend(), requestDto.getCateId());

        when(expenseService.updateExpense(expenseId, requestDto)).thenReturn(updatedDto);

        ExpenseDTO result = expenseController.updateExpense(expenseId, requestDto);

        assertNotNull(result);
        assertEquals(expenseId, result.getExpenseId());
    }

    @Test
    public void testDeleteExpense() {
        doNothing().when(expenseService).deleteExpense(expenseId);

        expenseController.deleteExpense(expenseId);

        verify(expenseService, times(1)).deleteExpense(expenseId);
    }
}
