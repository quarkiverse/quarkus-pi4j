package io.quarkiverse.pi4j.deployment;

import io.quarkiverse.pi4j.devui.Pi4jJsonRpcService;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.devui.spi.JsonRPCProvidersBuildItem;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;

public class Pi4jDevUiProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    CardPageBuildItem pages() {
        CardPageBuildItem card = new CardPageBuildItem();
        card.addPage(Page.webComponentPageBuilder()
                .title("Pi4J")
                .componentLink("qwc-pi4j.js")
                .icon("font-awesome-solid:microchip"));
        card.addPage(Page.externalPageBuilder("Pi4J Docs")
                .url("https://www.pi4j.com/")
                .icon("font-awesome-solid:book"));
        return card;
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    JsonRPCProvidersBuildItem rpcService() {
        return new JsonRPCProvidersBuildItem(Pi4jJsonRpcService.class);
    }
}
