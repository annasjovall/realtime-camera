/* A simple server in the internet domain using TCP
   The port number is passed as an argument */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include "camera.h"
#include <pthread.h>



// int try_open_camera(struct global_state* state)
// {
//     state->cam = camera_open();
//     if (!state->cam){ // Check if null
//         printf("axism3006v: Stream is null, can't connect to camera");
//         return ERR_OPEN_STREAM;
//     }
//     return 0;
// }

static pthread_mutex_t global_mutex = PTHREAD_MUTEX_INITIALIZER;

struct camera_state {
  byte* camera_byte;
  size_t frame_size;
};


void* camera_open_task(void *state)
{
  printf("anna\n");
  camera* cam = camera_open();
  frame* camera_frame = camera_get_frame(cam);
  struct camera_state* s = state;
  pthread_mutex_lock(&global_mutex);
  s.camera_byte = get_frame_bytes(camera_frame);
  s.frame_size = get_frame_size(camera_frame);
  pthread_mutex_unlock(&global_mutex);
  return (void*) (intptr_t) 0;
}


int main(int argc, char *argv[])
{
  struct sockaddr_in servaddr;
  struct sockaddr_in servaddr2;
  socklen_t socklen;
  printf("1\n");
  int listen_fd = socket(AF_INET, SOCK_STREAM, 0);
  bzero( &servaddr, sizeof(servaddr));
  servaddr.sin_family = AF_INET;
  servaddr.sin_addr.s_addr = htons(INADDR_ANY);
  servaddr.sin_port = htons(22000);
  printf("2\n");
  bind(listen_fd, (struct sockaddr *) &servaddr, sizeof(servaddr));
  printf("3\n");
  // at a time, the 11th one fails to.
  listen(listen_fd, 10);
  printf("4\n");
  pthread_t camera_open_thread;
  struct camera_state state;
  if(!pthread_create(&camera_open_thread, NULL, camera_open_task, &state)){
    while (1) {
      printf("4\n");
      int comm_fd = accept(listen_fd, (struct sockaddr*) &servaddr2, &socklen);
/*      printf("Size: %zu\n", frame_size);
      printf("Width: %zu\n", frame_width);
      printf("Heihgt: %zu\n", frame_height);*/

      //write(comm_fd, &frame_size_int, 10);
      pthread_mutex_lock(&global_mutex);
      write(comm_fd, state->camera_byte, state->frame_size);
    //  frame_free(camera_frame);
      pthread_mutex_unlock(&global_mutex);
    }
  }
  }
