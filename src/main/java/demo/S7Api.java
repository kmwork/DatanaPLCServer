package demo;

import com.github.s7connector.api.DaveArea;
import com.github.s7connector.api.S7Connector;
import com.github.s7connector.api.factory.S7ConnectorFactory;

import java.io.IOException;
import java.util.Arrays;

public class S7Api {

    public String getDataFromS7() throws IOException {

        S7Connector connector =
                S7ConnectorFactory
                        .buildTCPConnector()
                        .withHost("172.30.143.30")
                        .withRack(0) //optional
                        .withSlot(0) //optional
                        .build();

        byte[] bs = connector.read(DaveArea.DB, 3, 2, 0);

        String result = Arrays.toString(bs);
        System.out.println(result);

        connector.close();

        return result;
    }
}
