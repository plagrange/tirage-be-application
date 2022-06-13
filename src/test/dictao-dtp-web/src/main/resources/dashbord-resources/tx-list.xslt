<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:export="http://dictao.com/xsd/dtp/export/v2012_03">
    <xsl:output method="html" encoding="UTF-8"/>

    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="X-UA-Compatible" content="edge" />
                <title>DTP - Modified transaction list</title>
                <style type="text/css">
                    body
                    {
                    margin: 0px;
                    padding: 0;
                    font-size: 12px;
                    width: 100%;
                    height: 100%;
                    }

                    p
                    {
                    font-family: Verdana,Tahoma,Helvetica,sans-serif;
                    font-size: 12px;
                    padding-left: 70px;
                    font-weight: normal;
                    }

                    h1
                    {
                    font-size: 2.7em;
                    font-weight: normal;
                    margin: 15px 0 15px 30px;
                    font-family: Verdana,Tahoma,Helvetica,sans-serif;
                    letter-spacing: 0.1em;
                    padding-bottom: 10px;
                    border-bottom: 2px dashed #fc9605;
                    color: #0346a1;
                    }

                    h2
                    {
                    font-family: Verdana,Tahoma,Helvetica,sans-serif;
                    font-size: 1.2em;
                    padding-left: 30px;
                    font-weight: bold;
                    }

                    h3
                    {
                    font-family: Verdana,Tahoma,Helvetica,sans-serif;
                    font-size: 1.5em;
                    padding-left: 35px;
                    font-weight: normal;
                    margin-top: 20px;
                    color: #fc9605;
                    }

                    .reportTitle
                    {
                    font-family: Verdana,Tahoma,Helvetica,sans-serif;
                    color: #0346a1;
                    font-size: 14px;
                    font-weight: bold;
                    height: 30px;
                    padding-right: 10px;
                    border-bottom: 2px solid #6678B1;
                    min-width: 70px;
                    background-color: #ebf3fe;
                    white-space: nowrap;
                    }

                    .reportData
                    {
                    font-family: Verdana,Tahoma,Helvetica,sans-serif;
                    color: Black;
                    font-size: 12px;
                    font-weight: normal;
                    min-height: 20px;
                    padding-right: 10px;
                    padding-top: 4px;
                    padding-bottom: 4px;
                    border-bottom: 1px solid #6678B1;
                    white-space: nowrap;
                    }

                    .report
                    {
                    margin-right: 10px;
                    margin-left: 60px;
                    border-spacing: 0px;
                    empty-cells: show;
                    border-collapse: collapse;
                    }

                    tr:hover
                    {
                    background-color: #C4DBFC;
                    }

                    .header
                    {
                    background-color: #f0f0f1;
                    }
                </style>
                <script type="text/javascript">


                </script>
            </head>
            <body>

                <div class="header">
                    <h2>
                        From: <xsl:value-of select="export:export/export:from"/>
                    </h2>
                    <h2>
                        To: <xsl:value-of select="export:export/export:to"/>
                    </h2>
                    <h2>
                        Count: <xsl:value-of select="export:export/export:count"/>
                    </h2>
                </div>

                <h3>
                    Modified transaction list
                </h3>
                <table class="report" cellpadding="0" cellspacing="0">
                    <thead>
                        <tr>
                            <td class="reportTitle">
                                TransactionId
                            </td>
                            <td class="reportTitle">
                                Status
                            </td>
                            <td class="reportTitle">
                                Creation time
                            </td>
                            <td class="reportTitle">
                                Update time
                            </td>
                            <td class="reportTitle">
                                End time
                            </td>
                            <td class="reportTitle">
                                Company
                            </td>
                            <td class="reportTitle">
                                Business-type
                            </td>
                            <td class="reportTitle">
                                Business-Id
                            </td>
                        </tr>
                    </thead>
                    <tbody>

                        <xsl:for-each select="export:export/export:transactions/export:transaction">

                            <tr class="transaction-summary">
                                <td class="reportData">
                                    <a target="_blank">
                                        <xsl:attribute name="href">../transaction/<xsl:value-of select="export:transactionId"/>?transform=tx-summary</xsl:attribute>
                                        <xsl:value-of select="export:transactionId"/>
                                    </a>
                                </td>

                                <td class="reportData">
                                    <xsl:value-of select="export:status"/> / <xsl:value-of select="export:subStatus"/>
                                </td>
                                <td class="reportData">
                                    <xsl:value-of select="export:startTime"/>
                                </td>
                                <td class="reportData">
                                    <xsl:value-of select="export:updateTime"/>
                                </td>
                                <td class="reportData">
                                    <xsl:value-of select="export:endTime"/>
                                </td>
                                <td class="reportData">
                                    <xsl:value-of select="export:company"/>
                                </td>
                                <td class="reportData">
                                    <xsl:value-of select="export:businessType"/>
                                </td>
                                <td class="reportData">
                                    <xsl:value-of select="export:businessId"/>
                                </td>
                            </tr>

                        </xsl:for-each>

                    </tbody>
                </table>

            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
