<?xml version="1.0" encoding="UTF-8"?>
<!-- Dies ist die xsl-Datei um eine Diagnose herzustellen -->
<xsl:stylesheet version="1.0"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:fo="http://www.w3.org/1999/XSL/Format">
	
<xsl:param name="Schriftgroesse">11pt</xsl:param> 
<xsl:param name="Leftspace">9.5cm</xsl:param>
<xsl:param name="SchriftgroesseTabelle">8pt</xsl:param>
<xsl:param name="c" select="1"/>
			
<xsl:template match="/">
	<fo:root>
		<fo:layout-master-set>
			<fo:simple-page-master margin-right="2cm"
								   margin-left="2cm"
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
					<!-- Seitenzahl anzeigen -->
					<fo:page-number/> 
					
					<!-- Falzmarken anzeigen -->
					<fo:block-container absolute-position="fixed" top="105mm" left="5mm" width="5mm"> 
						<fo:block line-height="1pt"> 
							<fo:leader leader-length="5mm" leader-pattern="rule"/> 
						</fo:block> 
					</fo:block-container> 
					<fo:block-container absolute-position="fixed" top="210mm" left="5mm" width="5mm"> 
						<fo:block line-height="1pt">
							<fo:leader leader-length="5mm" leader-pattern="rule"/>
						</fo:block> 
					</fo:block-container>				
					
				</fo:block> 
			</fo:static-content> 
			<fo:flow flow-name="xsl-region-body">
				<fo:block font-size="{$Schriftgroesse}">
					<fo:table>
						<fo:table-column/>
						<fo:table-column/>
						<fo:table-body>
							<!-- Absender und Anschrift -->
							<fo:table-cell>
								<fo:block margin-top="3.2cm">
									<xsl:apply-templates select="Dokument/Absender"/>
									<xsl:apply-templates select="Dokument/Patient"/>
								</fo:block>			
							</fo:table-cell>
							<!-- Firmenlogo, Rechnung und Datum -->
							<fo:table-cell>
								<fo:block margin-left="1cm">
									<xsl:apply-templates select="Dokument/LogoInhalt"/>
									<xsl:apply-templates select="Dokument/Rechnung"/>
									<xsl:apply-templates select="Dokument/Datum"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-body>
					</fo:table>
					<fo:block-container position="absolute" top="60mm">
						<xsl:apply-templates select="Dokument/Vorwort"/>
						<xsl:apply-templates select="Dokument/Text"/>
						<fo:table table-layout="fixed" width="100%" font-size="{$SchriftgroesseTabelle}"
									border-separation="5.0pt">	

							<fo:table-column column-width="2cm" />
							<fo:table-column column-width="1.5cm"/> 
							<fo:table-column column-width="6.3cm"/>
							<fo:table-column column-width="2cm" />
							<fo:table-column column-width="1.3cm" />
							<fo:table-column column-width="2.2cm" />
						
							<fo:table-header text-align="center" width="100%" border-bottom="2.0pt double black" padding-start="10pt">						
								<fo:table-row>
									<fo:table-cell>
										<fo:block>Datum</fo:block>
									</fo:table-cell>
								
									<fo:table-cell>
										<fo:block>GebüH</fo:block>
									</fo:table-cell>
									
									<fo:table-cell>
										<fo:block>Bezeichnung der Leistung</fo:block>
									</fo:table-cell>
								
									<fo:table-cell>
										<fo:block>Einzelpreis</fo:block>
									</fo:table-cell>
								
									<fo:table-cell>
										<fo:block>Anzahl</fo:block>
									</fo:table-cell>
								
									<fo:table-cell>
										<fo:block>Gesamt</fo:block>
									</fo:table-cell>
								</fo:table-row>
										
							</fo:table-header>
						
							<fo:table-body>
								<xsl:for-each select="Dokument/Table/Row">
									<fo:table-row>
										<xsl:variable name="pos" select="position()"/>						
										<xsl:for-each select="//Row[$pos=position()]/Cell">
											<xsl:variable name="cell" select="position()"/>						
											<fo:table-cell padding-start="10pt" padding-end="5pt">						
												<xsl:if test="$cell=3">
													<fo:block text-align="left">												
														<xsl:apply-templates select="//Row[$pos=position()]/Cell[position()=$cell]"/>													
													</fo:block>
												</xsl:if>
												<xsl:if test="$cell!=3">
													<fo:block text-align="right">												
														<xsl:apply-templates select="//Row[$pos=position()]/Cell[position()=$cell]"/>													
													</fo:block>
												</xsl:if>	
											</fo:table-cell>
										</xsl:for-each>
									</fo:table-row>
								</xsl:for-each>
								<fo:table-row border-top="0.5cm solid white">
									<fo:table-cell>
										<fo:block />
									</fo:table-cell>
									<fo:table-cell>
										<fo:block />
									</fo:table-cell>
									<fo:table-cell>
										<fo:block />
									</fo:table-cell>
									<fo:table-cell>
										<fo:block />
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="{$Schriftgroesse}" text-align="right">
											Gesamtbetrag:
										</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-size="{$Schriftgroesse}" text-align="right">
											<xsl:apply-templates select="Dokument/Total"/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					
						<xsl:apply-templates select="Dokument/Greetings"/>
						<xsl:apply-templates select="Dokument/Abschluss"/>	
					</fo:block-container>				
				</fo:block>
			</fo:flow>	
		</fo:page-sequence>
	</fo:root>
</xsl:template>

<xsl:template match="LogoInhalt">
	<fo:block>
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

<xsl:template match="Datum">
	<fo:block font-size="{$Schriftgroesse}">
		Datum: <xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Rechnung">
	<fo:block font-size="{$Schriftgroesse}" margin-top="2em">
		Rechnung Nr.: <xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Absender">
	<fo:block font-size="8pt" text-decoration="underline">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Vorwort/Zeile">
	<fo:block font-size="{$Schriftgroesse}">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Vorwort">
	<fo:block margin-bottom="1cm" margin-top="2.5cm">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Text">
	<fo:block font-size="{$Schriftgroesse}"  margin-top="1cm">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="TextBlock">
	<fo:block font-size="{$Schriftgroesse}">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Abschluss">
	<fo:block font-size="{$Schriftgroesse}" margin-top="1.5em">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="Abschluss/Zeile">
	<fo:block>
		<xsl:apply-templates />
	</fo:block>
</xsl:template>

<xsl:template match="Greetings">
	<fo:block margin-top="1em">
		<xsl:apply-templates />
	</fo:block>
</xsl:template>

<xsl:template match="Greetings/Zeile">
	<fo:block>
		<xsl:apply-templates />
	</fo:block>
</xsl:template>

<xsl:template match="Total">
	<fo:block text-align="right" font-size="{$Schriftgroesse}">
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>
</xsl:stylesheet>
