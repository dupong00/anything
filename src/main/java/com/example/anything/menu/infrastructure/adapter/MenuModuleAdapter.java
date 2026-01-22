package com.example.anything.menu.infrastructure.adapter;


import com.example.anything.menu.internal.repository.MenuRepository;
import com.example.anything.menu.application.port.MenuModulePort;
import com.example.anything.menu.application.port.MenuResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuModuleAdapter implements MenuModulePort {
    private final MenuRepository menuRepository;
    
    @Override
    public List<MenuResponseDto> getMenusByIds(List<Long> menuIds){
        return menuRepository.findAllByIdIn(menuIds).stream()
                .map(menu -> new MenuResponseDto(menu.getId(), menu.getName()))
                .toList();
    }

    @Override
    public List<MenuResponseDto> getMenusByCategoryId(Long categoryId){
        return menuRepository.findAllByMenuCategoryId(categoryId).stream()
                .map(menu -> new MenuResponseDto(menu.getId(), menu.getName()))
                .toList();
    }
}
