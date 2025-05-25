package backend.academy.scrapper.reliability;

import backend.academy.scrapper.ExternalInitBase;
import backend.academy.scrapper.clients.UpdateLinkClient;
import backend.academy.scrapper.clients.update.UpdateLinkClientFacade;
import backend.academy.scrapper.services.LinkCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

public abstract class BaseReliability extends ExternalInitBase {
    @Autowired
    protected UpdateLinkClientFacade updateLinkClientFacade;

    @MockitoBean
    protected LinkCheckService linkCheckService;

    @MockitoSpyBean
    @Qualifier("updateLinkClientHTTP")
    protected UpdateLinkClient httpClient;

    @MockitoSpyBean
    @Qualifier("updateLinkClientKafka")
    protected UpdateLinkClient kafkaClient;
}
