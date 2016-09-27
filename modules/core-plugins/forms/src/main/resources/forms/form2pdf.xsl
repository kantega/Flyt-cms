<?xml version="1.0"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
    <xsl:template match="/">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4_start" page-height="297.00mm"
                                       page-width="210.00mm" margin-top="00.00mm" margin-bottom="00.00mm"
                                       margin-left="00.00mm" margin-right="00.00mm" background-color="white">
                    <fo:region-body margin-top="40.00mm" margin-bottom="10.00mm" margin-left="25.00mm"
                                    margin-right="20.00mm" background-color="white" border-style="solid"
                                    border-width="00.00mm" border-color="#0000FF"/>
                    <fo:region-before extent="40.00mm" background-color="white"
                                      border-style="solid" border-width="00.00mm" border-color="black"/>
                    <fo:region-after extent="10.00mm" background-color="white"
                                     border-style="solid" border-width="0.00mm" border-color="#000000"/>
                    <fo:region-start extent="25.00mm" background-color="white"
                                     border-style="solid" border-width="0.00mm" border-color="#000000"/>
                    <fo:region-end extent="20.00mm" background-color="white"
                                   border-style="solid" border-width="0.00mm" border-color="#000000"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="A4_start">
                <fo:static-content flow-name="xsl-region-before">
                    <fo:block font-size="18pt" padding-top="80pt" text-align="left" padding-bottom="50.00mm">
                        <xsl:value-of select="/formsubmission/metadata/formname" />
                    </fo:block>
                </fo:static-content>
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block font-size="8pt" text-align="end">
                        <fo:inline font-weight="normal" font-style="italic"><xsl:value-of select="/formsubmission/metadata/submissiondate" /></fo:inline>
                    </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-size="10pt">
                        <xsl:apply-templates select="/formsubmission/formvalues"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <xsl:template match="formvalues">
        <xsl:for-each select="formvalue">
            <fo:block font-family="Helvetica" font-size="10.00pt" font-weight="normal" font-style="normal" color="#888888" line-height="1.2" start-indent="10.00mm" background-color="#FFFFFF" border-width="0.5pt" border-style="dotted" margin-left="5mm" margin-right="1mm" space-after="2mm" border-color="#AAAAAA">
                <fo:block space-after="2mm" space-before="2mm" color="#888888" start-indent="10.00mm">
                    <xsl:value-of select="name"/>
                    <fo:leader leader-pattern="space" leader-length="0.2cm"/>
                    <fo:inline padding-left="10.00mm"  font-weight="normal" color="black"><xsl:value-of select="value"/></fo:inline>
                </fo:block>
            </fo:block>
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
