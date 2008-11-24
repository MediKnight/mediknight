<?xml version="1.0" encoding="UTF-8"?>
<!-- Dies ist die xsl-Datei um eine Verordnung herzustellen -->
<xsl:stylesheet version="1.0"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:fo="http://www.w3.org/1999/XSL/Format">
	
<xsl:param name="Schriftgroesse">11pt</xsl:param> 
<xsl:param name="Leftspace">8.5cm</xsl:param>
			
<xsl:template match="/">
	<fo:root>
		<fo:layout-master-set>
			<fo:simple-page-master margin-right="2cm"
								   margin-left="2.7cm"
								   margin-bottom="1.2cm"
								   margin-top="2.5cm"
								   page-width="21cm"
								   page-height="29.7cm"
								   master-name="first">
				<fo:region-body/>				
				<fo:region-after/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		
		<fo:page-sequence master-reference="first">
			<fo:static-content flow-name="xsl-region-after"> 
				<fo:block text-align-last="center"> 
					<fo:page-number/> 
				</fo:block> 
			</fo:static-content> 
			<fo:flow flow-name="xsl-region-body">
				<fo:block>
					<xsl:apply-templates select="Dokument/Name"/>	
					<xsl:apply-templates select="Dokument/Dauer"/>		
					<xsl:apply-templates select="Dokument/TagesDiagnosen"/>	
				</fo:block>
			</fo:flow>
		</fo:page-sequence>
	</fo:root>
</xsl:template>

<xsl:template match="Name">
	<fo:block  font-weight="bold" font-size="16pt">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Dauer">
	<fo:block font-weight="bold" font-size="{$Schriftgroesse}" margin-top="0.5cm">
		Dauerdiagnose:
	</fo:block>
	<fo:block font-size="{$Schriftgroesse}">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="TagesDiagnosen">
	<xsl:for-each select="child::*">
		<fo:block font-size="{$Schriftgroesse}" border="0.5pt solid black" margin-top="0.5cm">
			<fo:block font-weight="bold" margin-bottom="0.5em">
				Tagesdiagnose vom <xsl:apply-templates select="Datum"/>
			</fo:block>
			<fo:block>
				<xsl:apply-templates select="Text"/>
			</fo:block>
		</fo:block>		
	</xsl:for-each>	
</xsl:template>
</xsl:stylesheet>
