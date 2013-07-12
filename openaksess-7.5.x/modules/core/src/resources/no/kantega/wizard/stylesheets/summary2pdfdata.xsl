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
  xmlns:fi="http://apache.org/cocoon/forms/1.0#instance">

  <xsl:template match="wizardresults">
    <form>
      <template>
      <xsl:apply-templates select="form"/>
      </template>
    </form>
  </xsl:template>

  <xsl:template match="form">
    <xsl:element name="{@id}">
      <xsl:apply-templates select="widget|repeater|struct"/>
    </xsl:element>

  </xsl:template>

  <xsl:template match="widget">
    <xsl:element name="{@id}">
      <xsl:choose>
        <xsl:when test="values">
          <xsl:for-each select="values/*">
            <xsl:element name="{name()}"><xsl:value-of select="."/></xsl:element>
          </xsl:for-each>
        </xsl:when>

        <xsl:otherwise>
          <xsl:value-of select="value"/>
        </xsl:otherwise>

      </xsl:choose>
    </xsl:element>

  </xsl:template>


  <xsl:template match="widget" mode="repeater">

    <xsl:variable name="wid"><xsl:value-of select="substring-after(substring-after(@id, '.'), '.')"/></xsl:variable>

    <xsl:element name="{$wid}">
       <xsl:choose>
        <xsl:when test="values">
          <xsl:for-each select="values/*">
            <xsl:element name="{name()}"><xsl:value-of select="."/></xsl:element>
          </xsl:for-each>
        </xsl:when>

        <xsl:otherwise>
          <xsl:value-of select="value"/>
        </xsl:otherwise>

      </xsl:choose>
    </xsl:element>
   
  </xsl:template>

  <xsl:template match="repeater">
    <xsl:element name="{@id}">
      <xsl:apply-templates select="row"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="row">
    <row>
      <xsl:apply-templates select="widget" mode="repeater"/>
    </row>

  </xsl:template>


  <xsl:template match="struct">
    <xsl:element name="{@id}">
      <xsl:apply-templates select="widget" mode="struct"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="widget" mode="struct">

    <xsl:variable name="wid"><xsl:value-of select="substring-after(@id, '.')"/></xsl:variable>

    <xsl:element name="{$wid}">
       <xsl:choose>
        <xsl:when test="values">
          <xsl:for-each select="values/*">
            <xsl:element name="{name()}"><xsl:value-of select="."/></xsl:element>
          </xsl:for-each>
        </xsl:when>

        <xsl:otherwise>
          <xsl:value-of select="value"/>
        </xsl:otherwise>

      </xsl:choose>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>
