package com.itwillbs.factron.service.lotStructure;

import com.itwillbs.factron.entity.Lot;

import java.util.List;

public interface LotStructureService {

    public Void linkLotStructure(Lot parent, List<Lot> child);
}
