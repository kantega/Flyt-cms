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
  xmlns:wz="http://www.kantega.no/forms/wizard">

  <xsl:template match="results">
    <results>
       <xsl:apply-templates select="result/wz:wizard"/>
    </results>
  </xsl:template>

  <xsl:template match="wz:wizard">
     <row>
      <xsl:apply-templates select="wz:form/fi:form/fi:widgets"/>
    </row>
    </xsl:template>

  <xsl:template match="fi:widgets">
    <xsl:apply-templates select="fi:field|fi:booleanfield|fi:multivaluefield|fi:struct"/>
  </xsl:template>

  <xsl:template match="fi:field|fi:booleanfield">
    <xsl:if test="not(contains(@id, '.chooser_'))">
      <cell><xsl:attribute name="id"><xsl:value-of select="ancestor::wz:form/@id"/>.<xsl:value-of select="@id"/></xsl:attribute><xsl:value-of select="fi:value"/></cell>
    </xsl:if>
  </xsl:template>

  <xsl:template match="fi:multivaluefield">
    <cell><xsl:attribute name="id"><xsl:value-of select="ancestor::wz:form/@id"/>.<xsl:value-of select="@id"/></xsl:attribute><xsl:for-each select="fi:values/fi:value"><xsl:value-of select="."/><xsl:if test="position() != last()">,</xsl:if></xsl:for-each></cell>
  </xsl:template>

  <xsl:template match="fi:repeater">
      <xsl:apply-templates select="fi:repeater-row/fi:widgets"/>
  </xsl:template>
  <xsl:template match="fi:struct">
    <xsl:apply-templates select="fi:widgets"/>
  </xsl:template>

</xsl:stylesheet>