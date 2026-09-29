package com.mams.mams_backend.dto;

import java.util.List;

public record NetMovementDetailDTO(List<PurchaseDTO> purchases, List<TransferDTO> transfersIn,
                                   List<TransferDTO> transfersOut) {}
