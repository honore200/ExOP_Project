package com.omp.api.resource;

import com.omp.api.security.Secured;
import com.omp.common.entity.Asset;
import com.omp.common.service.AssetService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/assets")
@Secured
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssetResource {

    @Inject
    private AssetService assetService;

    @GET
    public List<AssetDTO> list(@QueryParam("type") String assetTypeCode) {
        List<Asset> assets = (assetTypeCode != null)
                ? assetService.findByTypeCode(assetTypeCode)
                : assetService.findAll();
        return assets.stream().map(AssetResource::toDTO).toList();
    }

    static AssetDTO toDTO(Asset a) {
        return new AssetDTO(a.getId(), a.getAssetCode(), a.getAssetName(), a.getAssetType().getCode(),
                a.getLocation() != null ? a.getLocation().getCode() : null,
                a.getStatus() != null ? a.getStatus().getCode() : null);
    }

    public record AssetDTO(Long id, String assetCode, String assetName, String assetTypeCode,
                            String locationCode, String statusCode) {
    }
}
