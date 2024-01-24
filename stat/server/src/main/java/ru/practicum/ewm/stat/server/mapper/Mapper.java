package ru.practicum.ewm.stat.server.mapper;

import ru.practicum.ewm.stat.dto.HitDtoCreate;
import ru.practicum.ewm.stat.dto.HitDtoGet;
import ru.practicum.ewm.stat.server.model.HitModel;

public class Mapper {
    public static HitModel mapToNewHitModel(HitDtoCreate hitDtoCreate) {
        HitModel hit = new HitModel();
        hit.setApp(hitDtoCreate.getApp());
        hit.setIp(hitDtoCreate.getIp());
        hit.setTimestamp(hitDtoCreate.getTimestamp());
        hit.setUri(hitDtoCreate.getUri());
        return hit;
    }

    public static HitDtoGet mapToHitDtoGet(HitModel hitModel, int hits) {


        return null;
    }
}
