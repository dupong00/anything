package com.example.anything.vote.dto;

import java.util.List;

public record CategorySelection (
        Long categoryId,
        List<Long> menuIds
){
}