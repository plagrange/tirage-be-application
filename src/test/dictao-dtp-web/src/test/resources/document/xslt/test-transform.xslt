<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8"/>
    <xsl:template match="/">
        <transformed-data>
            <xsl:value-of select="data"/>
        </transformed-data>
    </xsl:template>
</xsl:stylesheet>