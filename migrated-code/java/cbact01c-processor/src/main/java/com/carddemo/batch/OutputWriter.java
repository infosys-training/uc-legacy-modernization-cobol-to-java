package com.carddemo.batch;

import com.carddemo.batch.model.*;
import java.io.IOException;

public interface OutputWriter extends AutoCloseable {
    void writeOutRecord(OutAccountRecord record) throws IOException;
    void writeArrayRecord(ArrayRecord record) throws IOException;
    void writeVbRecord1(VbRecord1 record) throws IOException;
    void writeVbRecord2(VbRecord2 record) throws IOException;

    @Override
    void close() throws IOException;
}
