package com.example.jmeter;


import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;

public class CustomSampler extends AbstractJavaSamplerClient implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(CustomSampler.class);
    private static LoadTest loadTest = null;
    private static int iouValue = 0;
    private static String otherParty = null;

    private static final String NODE_ADDR = "node_addr";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String IOU_VALUE = "iouValue";
    private static final String PARTY_NAME = "partyName";

    public CustomSampler(){
        super();
        System.out.println("Custom Sampler created!");
    }

    @Override
    public void setupTest(JavaSamplerContext context){
        String node_addr = context.getParameter(NODE_ADDR);
        String username = context.getParameter(USERNAME);
        String password = context.getParameter(PASSWORD);

        try {
            loadTest = new LoadTest(node_addr, username, password);
        } catch (Exception e) {
            System.out.println("Unable to connect to Node");
        }

        iouValue = Integer.parseInt(context.getParameter(IOU_VALUE));
        otherParty = context.getParameter(PARTY_NAME);
        if(iouValue == 0) {
            System.out.println("IOU Value must be non-zero");
        }
        if(otherParty == null){
            System.out.println("partyName param is null");
        }

        super.setupTest(context);
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument(NODE_ADDR,"");
        defaultParameters.addArgument(USERNAME,"");
        defaultParameters.addArgument(PASSWORD,"");
        defaultParameters.addArgument(IOU_VALUE,"");
        defaultParameters.addArgument(PARTY_NAME,"");
        return defaultParameters;
    }


    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult sampleResult = new SampleResult();
        sampleResult.sampleStart();
        try {
            String message = loadTest.generateLoad(iouValue, otherParty);
            sampleResult.sampleEnd();;
            sampleResult.setSuccessful(Boolean.TRUE);
            sampleResult.setResponseCodeOK();
            sampleResult.setResponseMessage(message);
        } catch (Exception e) {
            logger.error("Request was not successfully processed", e);
            sampleResult.sampleEnd();
            sampleResult.setResponseMessage(e.getMessage());
            sampleResult.setSuccessful(Boolean.FALSE);
        }

        return sampleResult;
    }
}
