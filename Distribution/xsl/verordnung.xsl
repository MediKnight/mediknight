<?xml version="1.0" encoding="iso-8859-1"?>
<!-- Dies ist die xsl-Datei um eine Verordnung herzustellen -->
<xsl:stylesheet version="1.0"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:fo="http://www.w3.org/1999/XSL/Format">
	
<xsl:param name="Schriftgroesse">11pt</xsl:param> 
<xsl:param name="Leftspace">9.5cm</xsl:param>
<xsl:param name="Schrift">$Schrift$</xsl:param>
			
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
					<xsl:apply-templates select="Dokument/LogoInhalt"/>
					<xsl:apply-templates select="Dokument/Absender"/>
					<xsl:apply-templates select="Dokument/Patient"/>
					<xsl:apply-templates select="Dokument/Datum"/>
					<xsl:apply-templates select="Dokument/Betreff"/>
					<xsl:apply-templates select="Dokument/Text"/>
					<xsl:apply-templates select="Dokument/Abschluss"/>					
				</fo:block>
			</fo:flow>	
		</fo:page-sequence>
	</fo:root>
</xsl:template>

<xsl:template match="LogoInhalt">
	<fo:block  margin-left="{$Leftspace}">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="LogoInhalt/Ueberschrift">
	<fo:block font-size="{$Schriftgroesse}">
		<xsl:apply-templates/>	
	</fo:block>
</xsl:template>

<xsl:template match="LogoInhalt/Zeile">
	<fo:block font-size="{$Schriftgroesse}">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Anschrift/Zeile">
	<fo:block font-size="{$Schriftgroesse}">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Patient">
	<fo:block font-size="{$Schriftgroesse}">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Datum">
	<fo:block font-size="{$Schriftgroesse}" margin-left="{$Leftspace}">
		Datum: <xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Absender">
	<fo:block font-size="8pt" text-decoration="underline">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Betreff">
	<fo:block font-size="{$Schriftgroesse}" margin-top="1em" margin-bottom="2em">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Text">
	<fo:block font-size="{$Schriftgroesse}">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="TextBlock">
	<fo:block font-size="{$Schriftgroesse}">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Abschluss">
	<fo:block font-size="{$Schriftgroesse}" margin-top="2em">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>
</xsl:stylesheet>
