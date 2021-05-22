<#-- @ftlvariable name="data" type="com.pmurck.projectMatcher.DevEditData" -->
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
        <#if data.dev??>
            <h1>Desarrollador - Editar datos</h1>
        <#else>
            <h1>Desarrollador - Crear nuevo</h1>
        </#if>
        <form method="post">
            <div class="form-group">
                <label for="availabilityHours">Disponibilidad (horas)</label>
                <input type="number" class="form-control" id="availabilityHours" name="availabilityHours" placeholder="hs"
                       <#if data.dev??>value=${data.dev.availabilityHours?string.computer}</#if> required>
            </div>
            <div class="form-group">
                <label for="seniority">Seniority</label>
                <select class="form-control" id="seniority" name="seniority" required>
                    <#list data.seniorities as seniority>
                        <option value="${seniority.name()}" <#if data.dev?? && data.dev.seniority == seniority>selected</#if>>${seniority.desc}</option>
                    </#list>
                </select>
            </div>
            <#if data.dev??>
                <button type="submit" class="btn btn-primary">Actualizar</button>
            <#else>
                <button type="submit" class="btn btn-primary">Crear</button>
            </#if>
        </form>
    </div>
</main>
</html>

