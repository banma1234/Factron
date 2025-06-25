package com.itwillbs.factron.service.storage;

import com.itwillbs.factron.dto.storage.ResponseStorageDTO;

import java.util.List;

public interface StorageService {

    List<ResponseStorageDTO> getStorageByName(String name);
}
