package com.example.anything.vote.application.port;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WinnerMenuInfo {
    double longitude;
    double latitude;
    List<Long> winnerMenus;
}
