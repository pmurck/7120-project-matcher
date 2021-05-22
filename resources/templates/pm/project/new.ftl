<#-- @ftlvariable name="data" type="com.pmurck.projectMatcher.ProjectNewData" -->
<#import "/utils.ftl" as utils>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <title>Project Matcher</title>
    <!-- Bootstrap core CSS -->
    <link href="/static/bootstrap.css" rel="stylesheet">
</head>
<body>

<@utils.nav></@utils.nav>

<main role="main" class="container">
    <div class="jumbotron">
        <h1>Crear Proyecto</h1>
        <form method="post">
            <div class="form-group">
                <label for="projectName">Nombre del proyecto</label>
                <input type="text" class="form-control" id="projectName" name="projectName" placeholder="Nombre" required>
            </div>
            <h3>Requisitos horarios por grupo de Seniority</h3>
            <#list data.requirements as req>
                <div class="form-group">
                    <label for="${req.prefixedId()}">${req.name}</label>
                    <input type="number" class="form-control" id="${req.prefixedId()}" name="${req.prefixedId()}" placeholder="hs" required>
                </div>
            </#list>
            <button type="submit" class="btn btn-primary">Crear</button>
        </form>
    </div>
</main>

<!--
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js" integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj" crossorigin="anonymous"></script>
<script>window.jQuery || document.write('<script src="../assets/js/vendor/jquery.slim.min.js"><\/script>')</script><script src="../assets/dist/js/bootstrap.bundle.js"></script></body>
-->
</html>

