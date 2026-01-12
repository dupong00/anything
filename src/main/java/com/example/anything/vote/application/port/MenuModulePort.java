package com.example.anything.vote.application.port;

import com.example.anything.vote.dto.MenuResponseDto;
import java.util.List;

public interface MenuModulePort {
     List<MenuResponseDto> getMenusByIds(List<Long> menuIds);
}
