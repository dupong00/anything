package com.example.anything.menu.application.port;

import java.util.List;

public interface MenuModulePort {
     List<MenuResponseDto> getMenusByIds(List<Long> menuIds);
     List<MenuResponseDto> getMenusByCategoryId(Long categoryId);
}
