<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          XHTML with embedded RDFa

          XSLT 1.0

          Abdul Alabri, 2010-12

    -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml">

    <xsl:output method="html" media-type="application/xhtml+xml" indent="yes"/>
    <xsl:template name="footer">
        <div id="footer">
            <div class="footer-wrapper">
                <div id="siteinfo" class="vcard">
                    <div class="org fn">
                        <a href="http://www.uq.edu.au/">The University of Queensland</a>
                    </div>
                    <address id="uq-address" class="adr">
                        <span class="street-address">Brisbane</span>
                        <span class="locality">St Lucia</span>,
                        <span class="region">QLD</span>
                        <span
                                class="postal-code">4072
                        </span>
                    </address>
                    <div class="tel">+61 7 3365 1111</div>
                    <div class="campus">Other Campuses:<a href="http://www.uq.edu.au/ipswich/">UQ Ipswich</a>,
                        <br/>
                        <a
                                href="http://www.uq.edu.au/gatton/">UQ Gatton</a>,
                        <a
                                href="http://www.uq.edu.au/about/herston-campus">UQ Herston
                        </a>
                    </div>
                    <div class="directions">
                        <a href="http://www.uq.edu.au/maps/">Maps and Directions</a>
                    </div>
                    <div id="copyright">&#160; 2010 The University of Queensland</div>
                </div>
                <div id="footer-resources">
                    <h2>Supplemental Resources</h2>
                    <div id="navResources">
                        <h3>A Member of</h3>
                        <div class="member-logo">

                            <a href="http://www.universitas21.com/">
                                <img src="../images/logo-universitas.gif" border="0"
                                     width="25" height="25" alt="Universitas 21"/>
                            </a>
                            <a href="http://www.go8.edu.au/">
                                <img src="../images/logo-go8.gif" border="0" width="123"
                                     height="18" alt="Group of 8"/>
                            </a>
                        </div>
                        <p>
                            <a href="http://www.uq.edu.au/terms-of-use/">Terms of use</a>
                            |
                            <a
                                    href="http://www.uq.edu.au/feedback/">Feedback
                            </a>
                        </p>
                        <br/>
                        <p>Authorised by: Director of OMC</p>
                        <p>Maintained by:
                            <a href="mailto:webservices@uq.edu.au">webservices@uq.edu.au</a>
                        </p>
                        <p>ABN 63 942 912 684</p>
                        <p>CRICOS Provider No:
                            <a class="footerlinks" href="http://www.uq.edu.au/about/cricos-link">00025B</a>
                        </p>
                    </div>
                    <div id="navQuick">
                        <h3>Quick Links</h3>
                        <ul>
                            <li>
                                <a href="http://www.uq.edu.au/news/">For Media</a>
                            </li>
                            <li>
                                <a href="http://www.uq.edu.au/contacts/">Emergency Contact</a>
                            </li>
                        </ul>
                    </div>
                    <div id="navSocial">
                        <h3>Social Media</h3>
                        <ul>
                            <li>
                                <a href="http://www.uq.edu.au/itunes/">iTunes U</a>
                            </li>
                            <li>
                                <a href="http://www.flickr.com/photos/uqnews/sets/">Flickr</a>
                            </li>
                            <li>
                                <a href="http://twitter.com/uqnewsonline">Twitter</a>
                            </li>
                            <li>
                                <a href="http://www.youtube.com/universityqueensland">YouTube Channel</a>
                            </li>
                        </ul>
                    </div>
                </div>
                <div id="footer-right">
                    <div id="navExplore">
                        <h3>Explore</h3>
                        <ul>
                            <li>
                                <a href="http://www.alumni.uq.edu.au/giving">Giving to UQ</a>
                            </li>
                            <li>
                                <a href="http://www.uq.edu.au/departments/">Faculties &amp; Divisions</a>
                            </li>

                            <li>
                                <a href="http://www.uq.edu.au/staff/">Jobs at UQ</a>
                            </li>
                            <li>
                                <a href="http://www.uq.edu.au/contacts/">Contact UQ</a>
                            </li>
                            <li>
                                <a href="http://www.uq.edu.au/services/">Services &amp; Facilities</a>
                            </li>
                        </ul>
                    </div>
                    <div id="navLogos">
                        <h3>Need Help?</h3>
                        <p>
                            <a href="http://www.uq.edu.au/uqanswers/" class="opacity-toggle">
                                <img
                                        src="../images/button-uqanswers.png" width="102" height="28" border="0"
                                        alt="UQ Answers"/>
                            </a>
                        </p>
                        <p class="centenary">
                            <a href="http://www.uq.edu.au/centenary/" class="opacity-toggle">
                                <img
                                        src="../images/logo-centenary.png" width="102" height="69" border="0"
                                        alt="UQ Centenary"/>
                            </a>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </xsl:template>
</xsl:stylesheet>