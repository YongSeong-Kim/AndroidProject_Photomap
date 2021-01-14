from django.shortcuts import render
from django.contrib.auth.models import User
from django.contrib.auth import authenticate
from django.http import HttpResponse, JsonResponse
import json

def signin(request):
    data = json.loads(request.body)

    try:
        if request.method == 'POST':
            username = data['username']
            email = data['email']
            password = data['password']
            
            user = User.objects.create_user(username, email, password)
            user.save()

            return HttpResponse(status=200)
        elif request.method == 'GET':
            # TODO: In case method is GET
            pass
        else:
            return HttpResponse(status=400)
    except KeyError:
        return JsonResponse({'message' : 'INVALID KEY'}, status=400)

def login(request):
    data = json.loads(request.body)

    try:
        if request.method == 'POST':
            username = data['username']
            email = data['email']
            password = data['password']
            
            user = authenticate(username=username, password=password)
            if user is not None:
                return HttpResponse(status=200)
            else:
                return HttpResponse(status=400)

        elif request.method == 'GET':
            # TODO: In case method is GET
            pass
        else:
            return HttpResponse(status=400)
    except KeyError:
        return JsonResponse({'message' : 'INVALID KEY'}, status=400)