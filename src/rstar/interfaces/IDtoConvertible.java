package rstar.interfaces;

import rstar.dto.AbstractDTO;

public interface IDtoConvertible {
    public <T extends AbstractDTO> T toDTO();
}