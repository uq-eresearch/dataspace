<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>${registryTitle} - About</title>
    <jsp:include page="../include/head.jsp"/>
</head>
<body>
<jsp:include page="../include/header.jsp"/>
<div class="wrapper">
     <div class="content-top">
        <div class="pad-sides pad-top">
            <ul class="bread-crumbs-nav">
                <li class="bread-crumbs">
                    <a href="/">Home</a>
                </li>
                <li class="bread-crumbs-last">
                    About
                </li>
            </ul>
            <h1>About ${registryTitle}
            </h1>
        </div>
     </div>
    <div class="col-container pad-sides">


        <p>${registryTitle} catalogs the research data outputs
        of staff and students at the University of Queensland. It aims to improve the visibility and accessibility of
        UQ data to the wider world.</p>

        <p>The catalog contains descriptions of:</p>
        <ul>
            <li><a href="/collections">data collections</a>,</li>
            <li>the <a href="/agents">agents</a> (people and groups) that create and manage the data collections, </li>
            <li>the <a href="/activities">activities</a> (projects) that funded the data creation, and</li>
            <li>the <a href="/services">services</a> for accessing and manipulating the data</li>
        </ul>

        <p><img src="/images/ands_logo.gif">
            <br>
            UQ DataSpace aims to make UQ's research data visible and accessible at a national level by
        syndicating information to ANDS <a href="http://services.ands.org.au/home/orca/rda/">Research Data Australia</a>.</p>


        <h1>Describing your data</h1>

        <p>Have you got UQ research data you would like to share, or make better known?</p>

        <p>Your dataset may be large or small, digital or paper-based &mdash; it doesn't matter. We would like to
        identify and describe your data, and upload the description to this catalog and
        <a href="http://services.ands.org.au/home/orca/rda/index.php">Research Data Australia</a>. We are not
         offering to store your data &mdash; we only wish to describe it so that others can discover its existence (and
          re-use it if you give permission). If you are willing to be interviewed about your data, please contact
          <a href="mailto:info@dataspace.uq.edu.au">info@dataspace.uq.edu.au</a>.
        </p>



        <h1>Acknowledgements</h1>

        <p>The eResearch Lab within the UQ School of ITEE developed this system based on the Apache Abdera open
         source software. Feel free to contact us regarding the system and the technology used to produce it.</p>

         <p>This work supported by the <a href="http://www.ands.org.au/">Australian National Data Service</a> (ANDS).
          ANDS is supported by the Australian
          Government through the National Collaborative Research Infrastructure Strategy Program and the Education
          Investment Fund (EIF) Super Science Initiative.</p>



        <h1>Contact Information</h1>

        <h2>Postal Address</h2>

        <p>eResearch Lab - School of Information Technology and Electrical Engineering<br>
            <a href="http://www.uq.edu.au/">The University of Queensland</a> <br>
            Brisbane, QLD 4072.<br>

            Australia<br>

        </p>


        <h2>Location</h2>

        <p>
            <b>eResearch Lab</b><br>
            General Purpose South (GPS) Building (<a
                href="http://www.uq.edu.au/maps/index.phtml?menu=1&amp;sub_menu=0&amp;id=71&amp;z=1">No. 78</a>)<br>
            The office is on the seventh level, number 709.<br>

            <i>Office Hours:</i> 9:00am to 5:00pm Monday-Friday.<br>
            Later opening and earlier closing times may apply in vacation periods.

        <p><strong>Email:</strong> <a href="mailto:info@dataspace.uq.edu.au">info@dataspace.uq.edu.au</a><br>
            <strong>Website:</strong> <a
                    href="http://www.itee.uq.edu.au/~eresearch">http://www.itee.uq.edu.au/~eresearch</a><br>
            <strong>Phone:</strong> +61 7 336 54541<br>

            <strong>Fax:</strong> +61 7 3365 4999 </p>
        </p>

        <h1>Rights</h1>

        <p>Unless otherwise noted, all content &copy; The University of Queensland.</p>

        <p><a rel="license" href="http://creativecommons.org/licenses/by-nc/3.0/au/">
        <img alt="Creative Commons License" style="border-width:0"
        src="http://i.creativecommons.org/l/by-nc/3.0/au/88x31.png" /></a><br/>Descriptions of data collections,
        agents, activities and services are available under a <a rel="license"
        href="http://creativecommons.org/licenses/by-nc/3.0/au/">Creative Commons Attribution-NonCommercial
         3.0 Australia License</a>.

    </div>

    <div class="content-bottom">
        <div class="pad-sides pad-top pad-bottom">
            <div class="bread-crumbs-nav">
                <ul class="bread-crumbs-nav">
                    <li class="bread-crumbs">
                        <a href="/">Home</a>
                    </li>
                    <li class="bread-crumbs-last">
                        About
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>
<jsp:include page="../include/footer.jsp"/>
</body>
</html>