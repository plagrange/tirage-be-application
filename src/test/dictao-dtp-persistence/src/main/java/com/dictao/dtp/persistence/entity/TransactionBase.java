package com.dictao.dtp.persistence.entity;

public class TransactionBase {
    
    public static final String FIND_OPEN_TX_BY_UPDATETIMERANGE_QUERY =
            "SELECT t.transactionID FROM Step s INNER JOIN s.transaction t"
                    + " WHERE t.applicationID = :applicationID AND t.status = com.dictao.dtp.persistence.entity.TransactionStatusEnum.Opened"
                    + " GROUP BY t.transactionID"
                    + " HAVING MAX(s.creationTimetamp) BETWEEN :updateTimestampBegin AND :updateTimestampEnd"
                    + " ORDER BY MAX(s.creationTimetamp) DESC";
    public static final String COUNT_OPEN_TX_BY_UPDATETIMERANGE_QUERY =
            "SELECT count(t) FROM Transaction t"
                    + " WHERE t.applicationID = :applicationID"
                        + " AND t.status = com.dictao.dtp.persistence.entity.TransactionStatusEnum.Opened"
                        + " AND (SELECT MAX(s.creationTimetamp) FROM Step s WHERE s.transaction = t) BETWEEN :updateTimestampBegin AND :updateTimestampEnd";
    public static final String FIND_MODIFIED_TX_BY_UPDATETIMERANGE_QUERY =
        "SELECT t.transactionID FROM Step s INNER JOIN s.transaction t"
                + " WHERE t.applicationID = :applicationID"
                + " GROUP BY t.transactionID"
                + " HAVING MAX(s.creationTimetamp) BETWEEN :updateTimestampBegin AND :updateTimestampEnd"
                + " ORDER BY MAX(s.creationTimetamp) DESC";
    public static final String COUNT_MODIFIED_TX_BY_UPDATETIMERANGE_QUERY =
            "SELECT count(t) FROM Transaction t"
                    + " WHERE t.applicationID = :applicationID"
                        + " AND (SELECT MAX(s.creationTimetamp) FROM Step s WHERE s.transaction = t) BETWEEN :updateTimestampBegin AND :updateTimestampEnd";
    public static final String FIND_CLOSED_TX_BY_ENDTIMERANGE_QUERY =
            "Select t.transactionID from Transaction as t"
            + "     where t.applicationID = :applicationID and t.status = com.dictao.dtp.persistence.entity.TransactionStatusEnum.Closed"
            + "     and (t.endTimestamp >= :endTimestampBegin and t.endTimestamp < :endTimestampEnd)"
            + " order by t.endTimestamp DESC";
    public static final String COUNT_CLOSED_TX_BY_ENDTIMERANGE_QUERY =
            "Select count(t.id) from Transaction as t"
            + " where t.applicationID = :applicationID and t.status = com.dictao.dtp.persistence.entity.TransactionStatusEnum.Closed"
            + "     and (t.endTimestamp >= :endTimestampBegin and t.endTimestamp < :endTimestampEnd)";
    
    public static final String FIND_OPEN_TX_BY_UPDATETIMERANGE = "getOpenTransactionByStatusAndUpdateTimeRange";
    public static final String COUNT_OPEN_TX_BY_UPDATETIMERANGE = "getOpenTransactionCountByStatusAndUpdateTimeRange";
    public static final String FIND_MODIFIED_TX_BY_UPDATETIMERANGE = "getModifiedTransactionByStatusAndUpdateTimeRange";
    public static final String COUNT_MODIFIED_TX_BY_UPDATETIMERANGE = "getModifiedTransactionCountByStatusAndUpdateTimeRange";
    public static final String FIND_CLOSED_TX_BY_ENDTIMERANGE = "getClosedTransactionByStatusAndEndTimeRange";
    public static final String COUNT_CLOSED_TX_BY_ENDTIMERANGE = "getClosedTransactionCountByStatusAndEndTimeRange";
}
