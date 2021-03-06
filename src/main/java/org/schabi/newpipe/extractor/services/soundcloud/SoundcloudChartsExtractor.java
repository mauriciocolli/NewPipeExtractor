package org.schabi.newpipe.extractor.services.soundcloud;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.schabi.newpipe.extractor.Downloader;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.UrlIdHandler;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.kiosk.KioskExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItemsCollector;

import javax.annotation.Nonnull;

public class SoundcloudChartsExtractor extends KioskExtractor {
	private String url;

    public SoundcloudChartsExtractor(StreamingService service, String url, String nextPageUrl, String kioskId)
            throws ExtractionException {
        super(service, url, nextPageUrl, kioskId);
        this.url = url;
    }

    @Override
    public void onFetchPage(@Nonnull Downloader downloader) {
    }

    @Nonnull
    @Override
    public String getName() {
        return "< Implement me (♥_♥) >";
    }

    @Nonnull
    @Override
    public UrlIdHandler getUrlIdHandler() {
        return new SoundcloudChartsUrlIdHandler();
    }

    @Override
    public InfoItemPage getInfoItemPage() throws IOException, ExtractionException {
        if (!hasNextPage()) {
            throw new ExtractionException("Chart doesn't have more streams");
        }

        StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());
        nextPageUrl = SoundcloudParsingHelper.getStreamsFromApi(collector, nextPageUrl, true);

        return new InfoItemPage(collector, nextPageUrl);
    }

    @Nonnull
    @Override
    public StreamInfoItemsCollector getInfoItems() throws IOException, ExtractionException {
        StreamInfoItemsCollector collector = new StreamInfoItemsCollector(getServiceId());

        String apiUrl = "https://api-v2.soundcloud.com/charts" +
                "?genre=soundcloud:genres:all-music" +
                "&client_id=" + SoundcloudParsingHelper.clientId();

        if (getId().equals("Top 50")) {
            apiUrl += "&kind=top";
        } else {
            apiUrl += "&kind=trending";
        }

        List<String> supportedCountries = Arrays.asList("AU", "CA", "FR", "DE", "IE", "NL", "NZ", "GB", "US");
        String contentCountry = getContentCountry();
        if (supportedCountries.contains(contentCountry)) {
            apiUrl += "&region=soundcloud:regions:" + contentCountry;
        }

        nextPageUrl = SoundcloudParsingHelper.getStreamsFromApi(collector, apiUrl, true);
        return collector;
    }
}
