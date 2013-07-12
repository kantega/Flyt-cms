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

  <xsl:template match="wizardresults">
    <wizardresults>
      <xsl:apply-templates select="wz:wizard/wz:form"/>
    </wizardresults>
  </xsl:template>

  <xsl:template match="wz:wizard">
    <wizardresults>
      <xsl:apply-templates select="wz:form"/>
    </wizardresults>
  </xsl:template>
  
  <xsl:template match="wz:form">
    <form id="{@id}" name="{@name}">
      <xsl:apply-templates select="fi:form/fi:widgets"/>
    </form>

  </xsl:template>

  <xsl:template match="fi:widgets">
    <xsl:apply-templates select="fi:field|fi:booleanfield|fi:multivaluefield|fi:repeater|fi:struct"/>
  </xsl:template>

  <xsl:template match="fi:field|fi:booleanfield">
    <xsl:if test="not(contains(@id, '.chooser_'))">
      <xsl:variable name="val"><xsl:value-of select="fi:value"/></xsl:variable>
      <widget id="{@id}">
        <label>
          <xsl:value-of select="fi:label"/>
        </label>
        <value>
          <xsl:if test="fi:selection-list and fi:selection-list/fi:item[@value=$val]/fi:label">
            <xsl:attribute name="label"><xsl:value-of select="fi:selection-list/fi:item[@value=$val]/fi:label"/></xsl:attribute>
          </xsl:if>
          <xsl:value-of select="fi:value"/></value>
      </widget>
    </xsl:if>
  </xsl:template>

  <xsl:template match="fi:multivaluefield">
      <xsl:if test="not(contains(@id, '.chooser_'))">
        <widget id="{@id}">mwf
          <label>
            <xsl:value-of select="fi:label"/>
          </label>
          <values>
            <xsl:for-each select="fi:selection-list/fi:item">
              <xsl:variable name="val"><xsl:value-of select="@value"/></xsl:variable>
              <xsl:element name="value">
                <xsl:attribute name="label"><xsl:value-of select="fi:label"/></xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
                <xsl:choose>
                  <xsl:when test="../../fi:values/fi:value[text() = $val]">true</xsl:when>
                  <xsl:otherwise>false</xsl:otherwise>
                </xsl:choose>
              </xsl:element>
            </xsl:for-each>
          </values>
        </widget>
      </xsl:if>
    </xsl:template>

  <xsl:template match="fi:values">
    <values>
      <xsl:apply-templates select="fi:value"/>
    </values>
  </xsl:template>

  <xsl:template match="fi:field|fi:booleanfield|fi:multivaluefield" mode="repeater">

    <xsl:variable name="wid"><xsl:value-of select="substring-after(substring-after(@id, '.'), '.')"/></xsl:variable>

    <xsl:element name="{$wid}">
       <xsl:value-of select="fi:value"/>
    </xsl:element>
   
  </xsl:template>

  <xsl:template match="fi:repeater">
    <repeater id="{@id}">
      <label>
        <xsl:value-of select="fi:label"/>
      </label>

      <xsl:copy-of select="fi:headings"/>
      <xsl:apply-templates select="fi:repeater-row"/>
    </repeater>
  </xsl:template>

  <xsl:template match="fi:repeater-row">
    <row>
      <xsl:apply-templates select="fi:widgets"/>
    </row>

  </xsl:template>

  <xsl:template match="fi:struct">
    <struct id="{@id}">
      <label>
        <xsl:value-of select="fi:label"/>
      </label>
      

      <xsl:apply-templates select="fi:widgets"/>
    </struct>
  </xsl:template>
  
  <xsl:template match="fi:field|fi:booleanfield|fi:multivaluefield" mode="struct">
    <xsl:variable name="wid"><xsl:value-of select="substring-after(substring-after(@id, '.'), '.')"/></xsl:variable>

    <xsl:element name="{$wid}">
       <xsl:value-of select="fi:value"/>
    </xsl:element>
  </xsl:template>
  

</xsl:stylesheet>
