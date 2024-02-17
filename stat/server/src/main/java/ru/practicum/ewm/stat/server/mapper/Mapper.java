package ru.practicum.ewm.stat.server.mapper;

import ru.practicum.ewm.stat.dto.HitCreateDto;
import ru.practicum.ewm.stat.dto.HitGetDto;
import ru.practicum.ewm.stat.server.model.HitModel;

public class Mapper {
    public static HitModel mapToNewHitModel(HitCreateDto hitCreateDto) {
        HitModel hit = new HitModel();
        hit.setApp(hitCreateDto.getApp());
        hit.setIp(hitCreateDto.getIp());
        hit.setTimestamp(hitCreateDto.getTimestamp());
        hit.setUri(hitCreateDto.getUri());
        return hit;
    }

    public static HitGetDto mapToHitDtoGet(HitModel hitModel, int hits) {


        return null;
    }
}
