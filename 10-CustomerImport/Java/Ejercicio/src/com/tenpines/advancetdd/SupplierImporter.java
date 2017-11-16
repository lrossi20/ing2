package com.tenpines.advancetdd;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

public class SupplierImporter {

    public static final String INVALID_RECORD_TYPE = "Invalid record type";
    public static final String INVALID_ADDRESS_RECORD = "Invalid address record";
    private static final String NO_SUPPLIER_FOR_ADDRESS = "No supplier for address";
    private static final String INVALID_SUPPLIER_RECORD = "Invalid supplier record";
    private static final String INVALID_CUSTOMER_RECORD = "Invalid customer record";
    private final SupplierSystem system;
    private LineNumberReader lineReader;
    private String line;
    private String[] record;
    private Supplier supplier;

    public SupplierImporter(SupplierSystem system) {
        this.system = system;
    }

    public void from(Reader reader) throws Exception {
        lineReader = new LineNumberReader(reader);
        while (hasLinesToProcess()) {
            recordFromLine();
            parseRecord();
        }
    }

    private Boolean hasLinesToProcess() throws IOException {
        line = lineReader.readLine();
        return line != null;
    }

    private void recordFromLine() {
        record = line.split(",");
    }

    private void parseRecord() throws Exception {
        if (isSupplierRecord()) {
            loadSupplierFromRecord();
        } else if (isCustomerRecord()) {
            loadCustomerFromRecord();
        } else if (isAddressRecord()) {
            loadAddressInCustomerFromRecord();
        } else {
            throw new Exception(INVALID_RECORD_TYPE);
        }
    }

    private void loadAddressInCustomerFromRecord() throws Exception {
        if (record.length != 6) {
            throw new Exception(INVALID_ADDRESS_RECORD);
        }
        Address newAddress = new Address();
        newAddress.setStreetName(record[1]);
        newAddress.setStreetNumber(Integer.parseInt(record[2]));
        newAddress.setTown(record[3]);
        newAddress.setZipCode(Integer.parseInt(record[4]));
        newAddress.setProvince(record[5]);
        if (supplier == null) {
            throw new Exception(NO_SUPPLIER_FOR_ADDRESS);
        }
        supplier.addAddress(newAddress);
    }

    private void loadCustomerFromRecord() throws Exception {
        if (isNewCustomerRecord()) {
            loadNewCustomerFromRecord();
        } else if (isExistingCustomerRecord()) {
            loadExistingCustomerFromRecord();
        } else {
            throw new Exception(INVALID_CUSTOMER_RECORD);
        }
    }

    private void loadExistingCustomerFromRecord() throws Exception {
        if (record.length != 3) {
            throw new Exception(INVALID_CUSTOMER_RECORD);
        }
        Customer existingCustomer = supplier.customerWith(record[1]); //TODO: deberiamos ver si existe o no el customer?

        supplier.addCustomer(existingCustomer);
    }

    private void loadNewCustomerFromRecord() throws Exception {
        if (record.length != 5) {
            throw new Exception(INVALID_CUSTOMER_RECORD);
        }
        Customer customer = new Customer();
        customer.setFirstName(record[1]);
        customer.setLastName(record[2]);
        customer.setIdentificationType(record[3]);
        customer.setIdentificationNumber(record[4]);
        supplier.addCustomer(customer);
    }

    private void loadSupplierFromRecord() throws Exception {
        if (record.length != 4) {
            throw new Exception(INVALID_SUPPLIER_RECORD);
        }
        supplier = new Supplier();
        supplier.setName(record[1]);
        supplier.setIdentificationType(record[2]);
        supplier.setIdentificationNumber(record[3]);
        system.add(supplier);
    }

    public boolean isSupplierRecord() {
        return "S".equals(record[0]);
    }

    public boolean isCustomerRecord() {
        return isNewCustomerRecord() || isExistingCustomerRecord();
    }

    private boolean isAddressRecord() {
        return "A".equals(record[0]);
    }

    public boolean isNewCustomerRecord() {
        return "NC".equals(record[0]);
    }

    public boolean isExistingCustomerRecord() {
        return "EC".equals(record[0]);
    }
}
