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
                    padding-bottom:10px;
                    border-bottom: 2px dashed #fc9605;
                    color:#0346a1;
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
                    color:#fc9605;
                    }

                    h4
                    {
                    font-family: Verdana,Tahoma,Helvetica,sans-serif;
                    font-size: 1em;
                    padding-left: 45px;
                    text-decoration: underline;
                    margin-top: 20px;
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
                    background-color:#ebf3fe;
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
                    }

                    .reportTotal
                    {
                    font-family: Verdana,Tahoma,Helvetica,sans-serif;
                    color: Black;
                    font-size: 12px;
                    font-weight: bold;
                    min-height: 20px;
                    padding-right: 10px;
                    padding-top: 4px;
                    padding-bottom: 4px;
                    border-bottom: 1px solid #6678B1;
                    }

                    .report
                    {
                    margin-right: 10px;
                    margin-left: 60px;
                    border-spacing: 0px;
                    empty-cells:show;
                    border-collapse: collapse;
                    }

                    .separator
                    {
                    margin: 20px 30px 30px 35px;
                    border-bottom: 1px solid #ACA899;
                    font-family: Verdana,Tahoma,Helvetica,sans-serif;
                    font-size: 1.2em;
                    font-weight: bold;
                    padding: 5px 0 5px 0px;
                    }

                    tr:hover
                    {
                    background-color:#C4DBFC;
                    }

                    #haut{
                    background-color:#f0f0f1;
                    }
                    .copy{
                    font-size: 10px;
                    }

                    .details-container{
                    background-color:#FFFFFF !important;
                    }

                    .details{
                    margin: 16px;
                    padding: 2px 4px 8px 4px;
                    }

                    .details-table{
                    width: 100%;
                    border-spacing: 2px;
                    empty-cells:show;
                    border-collapse: collapse;
                    table-layout:fixed;
                    }

                    .details-header{
                    text-align: center;
                    color: #0346a1;
                    font-size: 14px;
                    font-weight: bold;
                    border-bottom: 2px solid #7BA1CE;
                    margin: 6px 2px 0 2px;
                    height: 26px;
                    background-color:#FFFFFF;
                    font-family: Verdana,Tahoma,Helvetica,sans-serif;
                    }

                    .details-title{
                    color: #000000;
                    font-size: 12px;
                    font-weight: bold;
                    height: 30px;
                    border-bottom: 2px solid #7BA1CE;
                    margin: 0 2px 0 2px;
                    background-color:#ebf3fe;
                    font-family: Verdana,Tahoma,Helvetica,sans-serif;
                    }

                    .details-data
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
                    }
                </style>
                <script type="text/javascript">


                </script>
            </head>
            <body>

                <xsl:for-each select="export:export/export:transactions/export:transaction">

                    <!-- Transaction summary -->

                    <div class="details">
                        <div class="details-header">
                            Transaction summary
                        </div>
                        <table class="details-table">
                            <tbody>
                                <tr>
                                    <td class="details-title">
                                        Param
                                    </td>
                                    <td class="details-title">
                                        Value
                                    </td>
                                </tr>

                                <tr>
                                    <td class="details-data">
                                        TransactionId
                                    </td>
                                    <td class="details-data">
                                        <a target="_blank">
                                            <xsl:attribute name="href"><xsl:value-of select="export:transactionId"/></xsl:attribute>
                                            <xsl:value-of select="export:transactionId"/>
                                        </a>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="details-data">
                                        Status
                                    </td>
                                    <td class="details-data">
                                        <xsl:value-of select="export:status"/> / <xsl:value-of select="export:subStatus"/>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="details-data">
                                        Creation time
                                    </td>
                                    <td class="details-data">
                                        <xsl:value-of select="export:startTime"/>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="details-data">
                                        Update time
                                    </td>
                                    <td class="details-data">
                                        <xsl:value-of select="export:updateTime"/>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="details-data">
                                        End time
                                    </td>
                                    <td class="details-data">
                                        <xsl:value-of select="export:endTime"/>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="details-data">
                                        Company
                                    </td>
                                    <td class="details-data">
                                        <xsl:value-of select="export:company"/>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="details-data">
                                        Business-type
                                    </td>
                                    <td class="details-data">
                                        <xsl:value-of select="export:businessType"/>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="details-data">
                                        Business-Id
                                    </td>
                                    <td class="details-data">
                                        <xsl:value-of select="export:businessId"/>
                                    </td>
                                </tr>

                            </tbody>
                        </table>
                    </div>


                    <!-- User access list -->


                    <div class="details">
                        <div class="details-header">
                            User access list
                        </div>
                        <table class="details-table">
                            <tbody>
                                <tr>
                                    <td class="details-title">
                                        Access Id
                                    </td>
                                    <td class="details-title">
                                        Status
                                    </td>
                                    <td class="details-title">
                                        User
                                    </td>
                                    <td class="details-title">
                                        First name
                                    </td>
                                    <td class="details-title">
                                        Last name
                                    </td>
                                    <td class="details-title">
                                        Workflow
                                    </td>
                                </tr>
                                <xsl:for-each select="export:userAccesses/export:userAccess">
                                    <tr>
                                        <td class="details-data">
                                            <xsl:value-of select="export:accessID"/>
                                        </td>
                                        <td class="details-data">
                                            <xsl:value-of select="export:status"/> / <xsl:value-of select="export:subStatus"/>
                                        </td>
                                        <td class="details-data">
                                            <xsl:value-of select="export:personalInfo/export:user"/>
                                        </td>
                                        <td class="details-data">
                                            <xsl:value-of select="export:personalInfo/export:firstName"/>
                                        </td>
                                        <td class="details-data">
                                            <xsl:value-of select="export:personalInfo/export:lastName"/>
                                        </td>
                                        <td class="details-data">
                                            <xsl:value-of select="export:uiInfo/export:ui"/>
                                        </td>
                                    </tr>
                                </xsl:for-each>
                            </tbody>
                        </table>
                    </div>

                    <!-- Document list -->


                    <div class="details">
                        <div class="details-header">
                            Document list
                        </div>
                        <table class="details-table">
                            <tbody>
                                <tr>
                                    <td class="details-title">
                                        Filename
                                    </td>
                                    <td class="details-title">
                                        Type
                                    </td>
                                    <td class="details-title">
                                        Mime-type
                                    </td>
                                </tr>
                                <xsl:choose>
                                    <xsl:when test="export:subStatus='Cancelled'">
                                        <xsl:for-each select="export:documents/export:document">
                                            <tr>
                                                <td class="details-data">
                                                        <xsl:value-of select="export:filename"/>
                                                </td>
                                                <td class="details-data">
                                                    <xsl:value-of select="export:type"/>
                                                </td>
                                                <td class="details-data">
                                                    <xsl:value-of select="export:mimeType"/>
                                                </td>
                                            </tr>
                                        </xsl:for-each>
                                    </xsl:when>
                                    <xsl:otherwise>                                
                                        <xsl:for-each select="export:documents/export:document">
                                            <tr>
                                                <td class="details-data">
                                                    <a>
                                                        <xsl:attribute name="href"><xsl:value-of select="export:url"/></xsl:attribute>
                                                        <xsl:value-of select="export:filename"/>
                                                    </a>
                                                </td>
                                                <td class="details-data">
                                                    <xsl:value-of select="export:type"/>
                                                </td>
                                                <td class="details-data">
                                                    <xsl:value-of select="export:mimeType"/>
                                                </td>
                                            </tr>
                                        </xsl:for-each>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </tbody>
                        </table>
                    </div>


                    <!-- Step list -->


                    <div class="details">
                        <div class="details-header">
                            Steps
                        </div>
                        <table class="details-table">
                            <tbody>
                                <tr>
                                    <td class="details-title">
                                        Date
                                    </td>
                                    <td class="details-title">
                                        Access Id
                                    </td>
                                    <td class="details-title">
                                        Name
                                    </td>
                                    <td class="details-title">
                                        Information
                                    </td>
                                    <td class="details-title">
                                        Document
                                    </td>
                                </tr>
                                <xsl:for-each select="export:steps/export:step">
                                    <tr>
                                        <td class="details-data">
                                            <xsl:value-of select="export:createTime"/>
                                        </td>
                                        <td class="details-data">
                                            <xsl:value-of select="export:userAccessID"/>
                                        </td>
                                        <td class="details-data">
                                            <xsl:value-of select="export:name"/>
                                        </td>
                                        <td class="details-data">
                                            <xsl:value-of select="export:information"/>
                                        </td>
                                        <td class="details-data">
                                            <xsl:value-of select="export:document/export:filename"/>
                                        </td>
                                    </tr>
                                </xsl:for-each>
                            </tbody>
                        </table>
                    </div>

                </xsl:for-each>

            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
