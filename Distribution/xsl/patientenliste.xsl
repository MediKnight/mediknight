<?xml version="1.0" encoding="UTF-8"?>
<!-- Dies ist die xsl-Datei um eine Patientenliste herzustellen -->
<xsl:stylesheet version="1.0"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:fo="http://www.w3.org/1999/XSL/Format">
	
<xsl:param name="Ueberschriftgroesse">16pt</xsl:param> 
<xsl:param name="Schriftgroesse">11pt</xsl:param> 
<xsl:param name="Leftspace">9.5cm</xsl:param>
<xsl:param name="SchriftgroesseTabelle">10pt</xsl:param>
			
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
				<fo:region-body margin-top="1cm"/>
				<fo:region-before/>
				<fo:region-after/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		
		<fo:page-sequence master-reference="first">
			<fo:static-content flow-name="xsl-region-before">
				<fo:block  font-weight="bold" font-size="{$Ueberschriftgroesse}">
					Patientenliste
				</fo:block>
			</fo:static-content>
			<fo:static-content flow-name="xsl-region-after"> 
				<fo:block text-align="center"> 
					<fo:page-number/>
				</fo:block> 
			</fo:static-content> 
			<fo:flow flow-name="xsl-region-body">
				<fo:block font-size="{$Schriftgroesse}">				
					<fo:table table-layout="fixed" width="100%" font-size="{$SchriftgroesseTabelle}" 
						border="2pt solid black" padding="10pt">
						
						<fo:table-column column-width="3cm" />
						<fo:table-column column-width="3cm" />
						<fo:table-column column-width="5cm" />
						<fo:table-column column-width="6cm" />
					
						<fo:table-header font-weight="bold" text-align="center" width="100%"
							border-bottom="2pt solid black">
							<fo:table-row keep-together.within-page="always">
								<fo:table-cell border-right="1pt solid black">
									<fo:block>Vorname</fo:block>
								</fo:table-cell>
							
								<fo:table-cell border-right="1pt solid black">
									<fo:block>Name</fo:block>
								</fo:table-cell>
							
								<fo:table-cell border-right="1pt solid black">
									<fo:block>Adresse</fo:block>
								</fo:table-cell>
							
								<fo:table-cell>
									<fo:block>Telefon</fo:block>
								</fo:table-cell>
														
							</fo:table-row>								
						</fo:table-header>
						
						<fo:table-body padding="10pt">
							<xsl:for-each select="Patienten/Patient">
								<fo:table-row keep-together.within-page="always" border-bottom="1pt solid black" padding="0pt">
									<xsl:variable name="pos" select="position()"/>						
										<xsl:for-each select="//Patient[$pos=position()]/Cell"></xsl:for-each>
									
										<fo:table-cell border-right="1pt solid black" padding="2pt">
											<fo:block text-align="left">												
												<xsl:apply-templates select="//Patient[$pos=position()]/Vorname"/>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell border-right="1pt solid black" padding="2pt">
											<fo:block text-align="left">												
												<xsl:apply-templates select="//Patient[$pos=position()]/Name"/>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell border-right="1pt solid black" padding="2pt">
											<fo:block text-align="left">	
												<xsl:apply-templates select="//Patient[$pos=position()]/Adresse1"/>
												<xsl:apply-templates select="//Patient[$pos=position()]/Adresse2"/>
												<xsl:apply-templates select="//Patient[$pos=position()]/Adresse3"/>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block text-align="left" padding="2pt">
												<xsl:apply-templates select="//Patient[$pos=position()]/Privat"/>
												<xsl:apply-templates select="//Patient[$pos=position()]/Arbeit"/>
												<xsl:apply-templates select="//Patient[$pos=position()]/Handy"/>
											</fo:block>
										</fo:table-cell>
								</fo:table-row>
							</xsl:for-each>
						</fo:table-body>
						
												
					</fo:table>
				</fo:block>
			</fo:flow>
		</fo:page-sequence>
	</fo:root>
</xsl:template>

<xsl:template match="Anrede">
	<fo:block>
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Titel">
	<fo:block>
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Vorname">
	<fo:block>
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Name">
	<fo:block>
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Adresse1">
	<fo:block>
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Adresse2">
	<fo:block>
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Adresse3">
	<fo:block>
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Privat">
	<fo:block>
		Privat:&#160;
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Arbeit">
	<fo:block>
		Arbeit:&#160;
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Handy">
	<fo:block>
		Handy:&#9;
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Fax">
	<fo:block>
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Email">
	<fo:block>
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

</xsl:stylesheet>