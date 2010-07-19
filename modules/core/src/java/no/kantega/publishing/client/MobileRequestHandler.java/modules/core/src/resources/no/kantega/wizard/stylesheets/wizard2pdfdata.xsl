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
  xmlns:wz="http://www.kantega.no/forms/wizard">

  <xsl:param name="formId"/>

  <xsl:template match="wz:wizard">
    <form>
      <template>
        <xsl:apply-templates select="wz:form"/>
      </template>
    </form>
  </xsl:template>

  <xsl:template match="wz:form">
    <xsl:element name="{@id}">
      <xsl:apply-templates select="fd:form"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="fd:form">
      <xsl:element name="{@id}">
        <xsl:apply-templates select="fd:widgets"/>
      </xsl:element>
    </xsl:template>

  <xsl:template match="fd:widgets">
    <xsl:element name="{@id}">
      <xsl:apply-templates select="fd:field|fd:booleanfield|fd:multivaluefield|fd:repeater|fd:struct"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="fd:field">
    <xsl:element name="{@id}">
      <xsl:choose>
        <xsl:when test="fd:selection-list"><xsl:value-of select="fd:selection-list/fd:item/@value"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="fd:label"/></xsl:otherwise>
      </xsl:choose>

    </xsl:element>
  </xsl:template>

  <xsl:template match="fd:multivaluefield">
      <xsl:element name="{@id}">
        <xsl:for-each select="fd:selection-list/fd:item">
          <value value="{@value}"><xsl:choose>
          <xsl:when test="position() mod 2 = 1">true</xsl:when>
          <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
        </value>
        </xsl:for-each>
      </xsl:element>
  </xsl:template>

  <xsl:template match="fd:booleanfield">
      <xsl:if test="not(contains(@id, 'chooser_'))">
      <xsl:element name="{@id}">
        <xsl:choose>
          <xsl:when test="position() mod 2 = 1">
            true
          </xsl:when>
          <xsl:otherwise>
            false
          </xsl:otherwise>
        </xsl:choose>
      </xsl:element>
    </xsl:if>
  </xsl:template>

  <xsl:template match="fd:struct">
    <xsl:element name="{@id}">
      <xsl:apply-templates select="fd:widgets"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="fd:repeater">
     <xsl:element name="{@id}">
        <row><xsl:apply-templates select="fd:widgets"/></row>
        <row><xsl:apply-templates select="fd:widgets"/></row>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
