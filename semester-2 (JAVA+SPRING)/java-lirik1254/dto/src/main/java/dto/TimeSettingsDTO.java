package dto;

import java.time.LocalTime;

public record TimeSettingsDTO(Settings notifyMood, LocalTime notifyTime) {}
