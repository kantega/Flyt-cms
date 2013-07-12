<?xml version="1.0" encoding="iso-8859-1"?>


<!--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fd="http://apache.org/cocoon/forms/1.0#definition"
  xmlns:ft="http://apache.org/cocoon/forms/1.0#template"
  xmlns:fi="http://apache.org/cocoon/forms/1.0#instance"
  xmlns:wz="http://www.kantega.no/forms/wizard"
  xmlns:html="http://www.w3.org/1999/xhtml"
  xmlns:jpath="http://apache.org/xsp/jpath/1.0">

  <xsl:param name="wizardId"/>
  <xsl:param name="aksessUrl"></xsl:param>
  <xsl:param name="wts"><xsl:text>                             </xsl:text></xsl:param>
  <xsl:output method="text"/>

  <xsl:template match="summary">
    <xsl:apply-templates select="wizardresults"/>
  </xsl:template>

<xsl:template match="wizardresults">
<xsl:apply-templates select="form"/>
</xsl:template>

<xsl:template match="form">
Trinn <xsl:value-of select="position()"/>: "<xsl:value-of select="@name"/>"
<xsl:apply-templates select="widget|repeater|struct"/>

  </xsl:template>

<xsl:template match="widget">
<xsl:text>     </xsl:text>       <xsl:value-of select="label"/>: <xsl:value-of select="substring($wts,0,15-string-length(label))"/><xsl:choose>
          <xsl:when test="value='true'">Ja</xsl:when>
          <xsl:when test="value='false'">Nei</xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="value/@label"><xsl:value-of select="value/@label"/></xsl:when>
              <xsl:otherwise><xsl:value-of select="value"/></xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise></xsl:choose>
<xsl:for-each select="values/*[text() = 'true']">
<xsl:if test="text() = 'true'"><xsl:value-of select="@label"/><xsl:if test="position() != last()">, </xsl:if></xsl:if>
</xsl:for-each>
<xsl:text>
</xsl:text>
</xsl:template>

  <xsl:template match="widget" mode="repeater">
<xsl:choose><xsl:when test="value='true'">Ja</xsl:when><xsl:when test="value='false'">Nei</xsl:when><xsl:otherwise><xsl:value-of select="value"/></xsl:otherwise></xsl:choose>
<xsl:for-each select="values/*[text() = 'true']">
<xsl:if test="text() = 'true'"><xsl:value-of select="@label"/><xsl:if test="position() != last()">, </xsl:if></xsl:if>
</xsl:for-each><xsl:text> | </xsl:text>
</xsl:template>


  <xsl:template match="repeater">
<xsl:text>
     </xsl:text><xsl:value-of select="label"/>: (tabell)
<xsl:text>        </xsl:text>
 <xsl:apply-templates select="fi:headings"/>
    <xsl:text>        </xsl:text>
    <xsl:for-each select="row">
<xsl:text>| </xsl:text>
 <xsl:apply-templates select="widget|repeater|struct" mode="repeater"/>
    </xsl:for-each>
    </xsl:template>

  <xsl:template match="struct">
<xsl:text>

     </xsl:text>
<xsl:value-of select="label"/>:
<xsl:apply-templates select="widget"/>
</xsl:template>

<xsl:template match="fi:headings">
<xsl:text>| </xsl:text>
 <xsl:for-each select="fi:heading">
<xsl:if test="text()">
<xsl:value-of select="."/><xsl:text> | </xsl:text>
</xsl:if>
</xsl:for-each>
<xsl:text>
</xsl:text>
</xsl:template>
   <xsl:template match="wz:wizard" mode="steps">
    <xsl:for-each select="wz:form">

          <div class="otherstep">
            <xsl:value-of select="@name"/>
          </div>
      <div class="otherstep">
            Oppsummering
          </div>
      <br/>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
