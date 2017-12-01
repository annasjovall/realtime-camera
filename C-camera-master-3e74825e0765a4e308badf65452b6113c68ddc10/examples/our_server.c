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


struct global_state {
  camera* cam;
  int comm_fd;
  int accepted;
  int camera_is_open;
};

static pthread_mutex_t global_mutex = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t global_cond = PTHREAD_COND_INITIALIZER;

void init_global_state(struct global_state* state)
{
    struct global_state* s = state;
    s->cam = NULL;
    s->accepted = 0;
    s->camera_is_open = 0;
}

void* read_task(void *state)
{
  printf("READ THREAD STARTED\n");
  pthread_mutex_lock(&global_mutex);
  struct global_state* s = state;
  pthread_mutex_unlock(&global_mutex);
  while (1) {
    char byte1[1];
    pthread_mutex_lock(&global_mutex);
    struct global_state* s = state;
    while(!s->accepted) {
      printf("bg_task: global_cond was signalled\n");
      pthread_cond_wait(&global_cond, &global_mutex);
    }
    read(s->comm_fd, byte1, 1);
    pthread_mutex_unlock(&global_mutex);
    printf("%c\n",byte1[0]);
  }
  return (void*) (intptr_t) 0;
}

void* write_task(void *state)
{
  printf("WRITE THREAD STARTED\n");
  pthread_mutex_lock(&global_mutex);
  struct global_state* s = state;
  while(!s->camera_is_open) {
    printf("bg_task: global_cond was signalled\n");
    pthread_cond_wait(&global_cond, &global_mutex);
  }
  camera* local_cam = s->cam;
  pthread_mutex_unlock(&global_mutex);
  while(1) {
    pthread_mutex_lock(&global_mutex);
    frame* camera_frame = camera_get_frame(s->cam);
    pthread_mutex_unlock(&global_mutex);
    byte* camera_byte = get_frame_bytes(camera_frame);
    size_t frame_size = get_frame_size(camera_frame);
    unsigned long long time_stamp = get_frame_timestamp(camera_frame);
    unsigned char bytes[8];
    bytes[0] = (time_stamp >> 56) & 0xFF;
    bytes[1] = (time_stamp >> 48) & 0xFF;
    bytes[2] = (time_stamp >> 40) & 0xFF;
    bytes[3] = (time_stamp >> 32) & 0xFF;
    bytes[4] = (time_stamp >> 24) & 0xFF;
    bytes[5] = (time_stamp >> 16) & 0xFF;
    bytes[6] = (time_stamp >> 8) & 0xFF;
    bytes[7] = time_stamp & 0xFF;
    //write(comm_fd, time_stamp, 100);
    pthread_mutex_lock(&global_mutex);
    while(s->accepted) {
      printf("bg_task: global_cond was signalled\n");
      pthread_cond_wait(&global_cond, &global_mutex);
    }
    write(s->comm_fd, camera_byte, frame_size);
    pthread_mutex_unlock(&global_mutex);
  }
  return (void*) (intptr_t) 0;
}

void* camera_open_task(void *state)
{
  printf("camera\n");
  pthread_mutex_lock(&global_mutex);
  struct global_state* s = state;
  s->cam = camera_open();
  if (!s->cam) {
    printf("Failed to create camera");
  }
  s->camera_is_open = 1;
  pthread_cond_broadcast(&global_cond);
  pthread_mutex_unlock(&global_mutex);
  printf("camera_open\n");
  struct sockaddr_in servaddr;
  int listen_fd = socket(AF_INET, SOCK_STREAM, 0);
  bzero( &servaddr, sizeof(servaddr));
  servaddr.sin_family = AF_INET;
  servaddr.sin_addr.s_addr = htons(INADDR_ANY);
  servaddr.sin_port = htons(22000);
  bind(listen_fd, (struct sockaddr *) &servaddr, sizeof(servaddr));
  listen(listen_fd, 10);
  printf("before accept");
  pthread_mutex_lock(&global_mutex);
  s->comm_fd = accept(listen_fd, (struct sockaddr*) NULL, NULL);
  s->accepted = 1;
  pthread_cond_broadcast(&global_cond);
  pthread_mutex_unlock(&global_mutex);
  printf("after accept\n");
  return (void*) (intptr_t) 0;
}


int main(int argc, char *argv[])
{

  printf("1\n");
  struct global_state state;
  init_global_state(&state);
  printf("init\n");
  pthread_t thread_ids[3];

  pthread_create(&thread_ids[0], NULL, camera_open_task, &state);
  pthread_create(&thread_ids[1], NULL, read_task, &state);
  pthread_create(&thread_ids[2], NULL, write_task, &state);

  printf("2\n");
  for (int i = 0; i < 3; i++) {
    printf("3\n");
    pthread_join(thread_ids[i], NULL);
  }
  printf("4\n");
  return 0;
}
