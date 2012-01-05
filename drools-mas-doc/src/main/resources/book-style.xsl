<!--
  ~ Copyright 2011 JBoss Inc
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~       http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xslthl="http://xslthl.sf.net" xmlns:d="http://docbook.org/ns/docbook"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    exclude-result-prefixes="xslthl" version="1.0">

    <xsl:import href="urn:docbkx:stylesheet" />

    <!-- Make hyperlinks blue and don't display the underlying URL -->
    <xsl:param name="ulink.show" select="0" />

    <xsl:attribute-set name="xref.properties">
        <xsl:attribute name="color">red</xsl:attribute>
    </xsl:attribute-set>

	<xsl:param name="header.image.filename" select="media/logo.png"/>
    <!-- Add any other template overrides here -->

</xsl:stylesheet>