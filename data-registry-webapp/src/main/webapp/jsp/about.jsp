<%@ page import="net.metadata.dataspace.app.RegistryApplication" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title><%=RegistryApplication.getApplicationContext().getRegistryTitle()%> - About</title>
    <jsp:include page="../include/head.jsp"/>
</head>
<body>
<jsp:include page="../include/header.jsp"/>
<div class="wrapper">
    <ul class="bread-crumbs-nav">
        <li class="bread-crumbs">
            <a href="/">Home</a>
        </li>
        <li class="bread-crumbs-last">
            About
        </li>
    </ul>
    <div class="home-content">
        <h1>About <%=RegistryApplication.getApplicationContext().getRegistryTitle()%>
        </h1>

        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce vel lorem placerat odio auctor ornare vel et
            nunc. Nam ac condimentum dolor. Vivamus urna eros, sodales id tempus at, congue faucibus nisi. Etiam quis
            augue a tortor interdum semper. Nam fringilla, nulla et porttitor faucibus, odio mi aliquet urna, ac aliquam
            augue nisl commodo velit. Pellentesque non mi eget velit ornare accumsan vehicula id sem. Vestibulum
            tincidunt, nibh id ornare interdum, libero dolor ornare lectus, ut auctor sem diam eu mi. Nunc adipiscing
            nulla non eros scelerisque feugiat. Cras purus neque, fermentum non pellentesque quis, aliquam nec lectus.
            Donec bibendum nisi a enim rhoncus egestas.</p>

        <p>Quisque vel nunc quis erat tincidunt porttitor. Sed eget erat non diam volutpat bibendum. Etiam gravida, ante
            vitae consectetur convallis, lectus dolor porttitor nulla, at porta leo elit at felis. Quisque ac arcu
            tellus, non blandit tortor. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed dapibus malesuada
            tempus. Cras porttitor fermentum nibh sit amet pretium. Pellentesque feugiat arcu eget risus semper sit amet
            placerat magna imperdiet. Duis posuere odio vitae justo luctus sit amet aliquet tortor bibendum. Cras sed
            malesuada enim. Nulla sit amet eros erat, eget faucibus erat. Nulla quis ante nec ipsum ultrices
            condimentum. Mauris est metus, luctus at euismod non, ultricies ut felis. Donec commodo bibendum mauris sed
            varius. Fusce at odio massa, a facilisis justo. Morbi a auctor erat. Curabitur ligula nunc, euismod id
            aliquam sit amet, tincidunt et sapien. Integer eget sapien eget diam egestas egestas. Pellentesque gravida
            malesuada pellentesque. Nulla in lorem eu purus convallis scelerisque ut eu diam.</p>

        <div class="clear">
            <br/>
        </div>
        <h1>Contact Information</h1>

        <h2>Website Development</h2>

        <p>This website is developed by the eResearch lab at the University of Queensland in Brisbane, Australia.
            Feel free to contact us regarding the website and the technology used in producing it.</p>

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

        <div class="clear">
            <br/>
        </div>
        <h1>Content</h1>

        <p>The content of this site is managed by .....</p>

        <div class="clear">
            <br/>
        </div>
        <h1>License</h1>

        <p>License text ..</p>

    </div>
</div>
<jsp:include page="../include/footer.jsp"/>
</body>
</html>