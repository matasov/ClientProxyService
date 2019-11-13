<#import "parts/common.ftl" as c>
<#import "parts/login.ftl" as l>

<@c.page>
Login page
<@l.login "/manager/login" />
<a href="/registration">Add new user</a>
</@c.page>
