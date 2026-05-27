import bs4
import re
import os

with open('eventsphere.html', 'r', encoding='utf-8') as f:
    soup = bs4.BeautifulSoup(f, 'html.parser')

def extract_and_wrap(page_id, output_path):
    page_div = soup.find('div', id=page_id)
    if not page_div: return
    
    if 'active' in page_div.get('class', []):
        page_div['class'].remove('active')
        
    html_content = str(page_div)
    
    html_content = html_content.replace("onclick=\"showPage('events')\"", "th:href=\"@{/events}\"")
    html_content = html_content.replace("onclick=\"showPage('login')\"", "th:href=\"@{/auth/login}\"")
    html_content = html_content.replace("onclick=\"showPage('register')\"", "th:href=\"@{/auth/register}\"")
    html_content = html_content.replace("onclick=\"showPage('student')\"", "th:href=\"@{/student/dashboard}\"")
    html_content = html_content.replace("onclick=\"showPage('organizer')\"", "th:href=\"@{/organizer/dashboard}\"")
    html_content = html_content.replace("onclick=\"showPage('admin')\"", "th:href=\"@{/admin/dashboard}\"")
    html_content = html_content.replace("onclick=\"showPage('landing')\"", "th:href=\"@{/}\"")
    
    # Replace buttons that are now anchors
    html_content = re.sub(r'<button([^>]+)th:href="([^"]+)"([^>]*)>(.*?)</button>', r'<a\1th:href="\2"\3>\4</a>', html_content, flags=re.DOTALL)
    
    html = f"""<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EventSphere</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700;800;900&family=DM+Sans:ital,wght@0,300;0,400;0,500;0,600;1,400&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" th:href="@{{/static/css/style.css}}">
</head>
<body>
    <div class="toast-container" id="toastContainer"></div>
    <div class="confetti-container" id="confettiContainer"></div>
    
    {html_content}
    
    <form id="logoutForm" th:action="@{{/logout}}" method="POST" style="display:none;"></form>
    <script th:src="@{{/static/js/app.js}}"></script>
</body>
</html>"""

    # ensure dir exists
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    with open(output_path, 'w', encoding='utf-8') as out:
        out.write(html)

pages = {
    'page-landing': 'src/main/resources/templates/index.html',
    'page-login': 'src/main/resources/templates/auth/login.html',
    'page-register': 'src/main/resources/templates/auth/register.html',
    'page-student': 'src/main/resources/templates/student/dashboard.html',
    'page-organizer': 'src/main/resources/templates/organizer/dashboard.html',
    'page-admin': 'src/main/resources/templates/admin/dashboard.html',
    'page-events': 'src/main/resources/templates/events/list.html',
    'page-eventdetail': 'src/main/resources/templates/events/details.html'
}

for pid, path in pages.items():
    extract_and_wrap(pid, path)
