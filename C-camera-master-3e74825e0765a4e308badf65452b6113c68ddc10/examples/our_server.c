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

struct global_state {
  camera* cam;
  int camera_is_open;
  int movie_mode;
  int read_port;
  int write_port;
};

void init_global_state(struct global_state* state)
{
    struct global_state* s = state;
    s->cam = NULL;
    s->movie_mode=0;
}

void* read_task(void *state)
{
  struct global_state* s = state;
  struct sockaddr_in servaddr;
  int listen_fd = socket(AF_INET, SOCK_STREAM, 0);
  bzero( &servaddr, sizeof(servaddr));
  servaddr.sin_family = AF_INET;
  servaddr.sin_addr.s_addr = INADDR_ANY;
  servaddr.sin_port = htons(s->read_port);
  if(bind(listen_fd, (struct sockaddr *) &servaddr, sizeof(servaddr))){
    perror("bind listenfd");
  }
  if(listen(listen_fd, 10)){
    perror("listen listenfd");
  }
  int comm_fd_read = accept(listen_fd, (struct sockaddr*) NULL, NULL);
  while (1) {
    usleep(100000);
    char read_byte[1];
    if(read(comm_fd_read, read_byte, 1) < 0){
      comm_fd_read = accept(listen_fd, (struct sockaddr*) NULL, NULL);
    }
    printf("%hhx", read_byte[0]);
    if(read_byte[0]){
      s->movie_mode=1;
      printf("ENTERING MOVIE MODE\n");
    }else if(!read_byte[0]){
      s->movie_mode=0;
      printf("ENTERING IDLE MODE\n");
    }
  }
  return (void*) (intptr_t) 0;
}

void* write_task(void *state)
{
  camera* cam = camera_open();
  struct global_state* s = state;
  if (!cam) {
    printf("Failed to create camera\n");
  }
  struct sockaddr_in servaddr;
  int listen_fd = socket(AF_INET, SOCK_STREAM, 0);
  bzero( &servaddr, sizeof(servaddr));
  servaddr.sin_family = AF_INET;
  servaddr.sin_addr.s_addr = INADDR_ANY;
  servaddr.sin_port = htons(s->write_port);
  if(bind(listen_fd, (struct sockaddr *) &servaddr, sizeof(servaddr))){
    perror("bind listenfd");
  }
  if(listen(listen_fd, 10)){
    perror("listen listenfd");
  }
  int comm_fd_write = accept(listen_fd, (struct sockaddr*) NULL, NULL);
  while(1) {
    if(s->movie_mode){
      usleep(1000);
    }else{
      usleep(10000);
    }
    frame* camera_frame = camera_get_frame(cam);
    byte* camera_byte = get_frame_bytes(camera_frame);
    size_t frame_size = get_frame_size(camera_frame);
    time_t time_stamp = time(NULL);
    byte header[8];
    header[0] = (frame_size >> 24) & 0xFF;
    header[1] = (frame_size >> 16) & 0xFF;
    header[2] = (frame_size >> 8) & 0xFF;
    header[3] = frame_size & 0xFF;
    header[4] = (time_stamp >> 24) & 0xFF;
    header[5] = (time_stamp >> 16) & 0xFF;
    header[6] = (time_stamp >> 8) & 0xFF;
    header[7] = time_stamp & 0xFF;
    if(write(comm_fd_write, &header, 8) < 0){
      comm_fd_write = accept(listen_fd, (struct sockaddr*) NULL, NULL);
    }else if(write(comm_fd_write, camera_byte, frame_size) < 0){
      comm_fd_write = accept(listen_fd, (struct sockaddr*) NULL, NULL);
    }
    frame_free(camera_frame);
  }
  return (void*) (intptr_t) 0;
}


int main(int argc, char *argv[])
{
  struct global_state state;
  pthread_t thread_ids[2];

  if(argc==3) {
      state.read_port = atoi(argv[1]);
      state.write_port = atoi(argv[2]);
  } else {
      state.read_port = 22000;
      state.write_port = 19999;
  }

  pthread_create(&thread_ids[0], NULL, read_task, &state);
  pthread_create(&thread_ids[1], NULL, write_task, &state);

  pthread_join(thread_ids[0], NULL);
  pthread_join(thread_ids[1], NULL);

  return 0;
}
